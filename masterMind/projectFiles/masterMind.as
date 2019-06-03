;==================================================
;--------------------------------------------------
;					CONSTANTES
;--------------------------------------------------
;==================================================

Xis 			EQU 'X'
Oos 			EQU 'O'
fim_str_g       EQU '!'
Mais            EQU '+'
Traco           EQU '-'
Barra           EQU '|'
Espaco          EQU ' '

Num_Jog			EQU	12

SP_INICIO 		EQU FDFFh

Masc_Ult3B		EQU 0000000000000111b			
INT_MASK_ADDR 	EQU FFFAh	
INT_MASK_INI  	EQU 847Eh
INT_MASK_INPT	EQU 807Eh

randomizer		EQU AFCBh
EndCh			EQU 8000h
EndIn 			EQU 8001h						;endereco do input do user
EndOut			EQU 8002h						
EndCursor       EQU 8003h
EndJog			EQU 8004h
EndHScore		EQU 8005h
LCD_Prim_dig	EQU 8001h  						;1000 0000 0000 0001
LCD_Seg_dig		EQU 8002h  						;1000 0000 0000 0010	
LCD_limpa_1		EQU 8030h  						;1000 0000 0010 0000		
LCD_limpa_2		EQU 8031   						;1000 0000 0010 0001

IO				EQU FFFEh
IO_CURSOR		EQU FFFCh

IO_LEDS			EQU FFF8h
LED_INI			EQU FFFFh

PONT_ATUAL1		EQU FFF0h						;Endereco do primeiro 7seg
PONT_ATUAL2		EQU FFF1h						;Endereco do segundo 7seg
LCD_WRITE		EQU FFF5h
LCD_CONTROL		EQU FFF4h

TEMP_END		EQU FFF6h
TEMP_CONTROL	EQU FFF7h
NUM_MS			EQU 5




				ORIG 8018h
str_titulo_ini	STR	 'BEM VINDO AO MASTERMIND!'
str_ganhou      STR  'Parabens, acertou no numero!'
str_perdeu1     STR  'Acabaram as jogadas validas...!'
str_perdeu2     STR  'How do you feel losing against a 16 bit cpu?!'
str_espera		STR  'Por favor escreva a sua jogada com os botoes: !'
str_ecra_ini	STR  'Prima IA para iniciar!'
str_reinicia	STR  'Prima IA para reiniciar!'
str_autores 	STR  'Creditos: Afonso e Daniel !'
str_instr		STR	 'Use os botoes I1 a I6 para inserir a sua jogada!'
str_pr_com		STR  'Pronto para começar?!'
str_titulo_inp	STR  'INTRODUZA O SEU INPUT!'
str_perdeu_tmp	STR	 'Oops. Acabou o tempo...!'
str_chave		STR	 'A chave correta e:!'


				ORIG FE01h
INT1			WORD INT1F
INT2			WORD INT2F
INT3			WORD INT3F
INT4			WORD INT4F
INT5			WORD INT5F
INT6			WORD INT6F

				ORIG FE0Ah
INTA			WORD INTAF
				
				ORIG FE0Fh
INTF			WORD INTFF				
				
;==================================================
;--------------------------------------------------
;					INICIO
;--------------------------------------------------
;==================================================

				ORIG 0000h
				JMP inicio
				
				
INT1F:			OR R3, 1
				RTI

INT2F:			OR R3, 2
				RTI

INT3F:			OR R3, 3
				RTI

INT4F:			OR R3, 4
				RTI

INT5F:			OR R3, 5
				RTI

INT6F:			OR R3, 6
				RTI

INTAF:			MOV R4, 0 	
				RTI
				

INTFF:			SHR	R1, 1
				RTI
				
inicio:			MOV R7, SP_INICIO				;inicializa SP
				MOV SP, R7		
				MOV R7, FFFFh                   ;inicializa cursor
				MOV M[IO_CURSOR], R7
				MOV R6, R0                      ;primeira posicao do cursor
				MOV M[EndCursor], R6
				MOV R7, 0                       ;inicializa highscore
				MOV M[EndHScore], R7
				MOV R1, M[EndHScore]
				PUSH R1
				CALL escreve_HS                 ;escreve highscore no LCD
				
novo_jogo:		MOV R1, R0						;reinicia pontuacao atual
				PUSH R1
				CALL escreve_pontos
				
				MOV R1, LED_INI					;reinicia LEDS
				MOV M[IO_LEDS], R1
				
				CALL cls						;Imprime o ecra inicial
				
				MOV R1, 0100h
				MOV M[EndCursor], R1
				MOV R1, str_titulo_ini
				MOV R2, 27						;Posicao coluna da string
				PUSH R2
				PUSH R1
				CALL print_str
				
				MOV R1, 0701h
				MOV M[EndCursor], R1
				MOV R1, str_ecra_ini
				MOV R2, 27
				PUSH R2
				PUSH R1
				CALL print_str

				MOV R1, 0F00h
				MOV M[EndCursor], R1
				MOV R1, str_autores
				MOV R2, 26
				PUSH R2
				PUSH R1
				CALL print_str


espera_IA:		ENI
				MOV R7, INT_MASK_INI			;inicializa interrupcoes (mascara)
				MOV M[INT_MASK_ADDR], R7
				MOV R4, 1                                       ;FLAG DE ESPERA IA
				MOV R1, R0                                      ;Contador de ciclos -> gerador aleatorio de chave
ccl_espera_IA:	INC R1											;loop de espera pela interrupcao e conta ciclos
				CMP R4, 0
				BR.NZ ccl_espera_IA
				DSI
				PUSH R0
				PUSH R1											;em R1 esta a chave crua								
				CALL trata_chave								;transforma a chave crua numa chave valida
				POP R1
				MOV M[EndCh], R1								;guarda a chave no endereco
				MOV R7, Num_Jog
				MOV M[EndJog], R7
				CALL cls
				MOV R7, 0100h
				MOV M[EndCursor], R7
				MOV R6, str_titulo_inp
				MOV R5, 28
				PUSH R5
				PUSH R6
				CALL print_str
				MOV R7, 0403h
				MOV M[EndCursor], R7
				MOV R7, INT_MASK_INPT							;inicializa interrupcoes (mascara)
				MOV M[INT_MASK_ADDR], R7 
				
				
inicio_jogada:	MOV R1, LED_INI									;inicializa LEDS	 	
				MOV R4, 4                                       ;Inicializa conta algarismos do input
				MOV R2, R0                                      ;Inicializa o registo do input
				MOV R3, R0                                      ;Inicializa flag de input
				MOV R5, R1										;Inicializa flag de tempo
				MOV R7, M[EndCursor]
				;ADD R7, 0100h
				;AND R7, FF00h
				;OR R7, 3h
				MOV M[EndCursor], R7
				ENI

atualiza_tmp:	MOV M[IO_LEDS], R1
				CMP R1, R0
				JMP.Z perdeu_tempo
				MOV R5, R1                                      ;Atualizar flag de tempo

				MOV R7, TEMP_END								;Inicializar Cronometro
				MOV R6, NUM_MS
				MOV M[R7], R6
				MOV R7, TEMP_CONTROL
				MOV R6, 1
				MOV M[R7], R6
				
ccl_espera_inp:	CMP R5, R1										;Mudar LEDS?
				BR.NZ atualiza_tmp
				CMP R3, R0										;Mudar inp?
				BR.Z ccl_espera_inp								

atualiza_inp:	OR R2, R3                                       ;Atualiza input
				ROL R2, 3                                       ;Prepara prox input
				MOV R6, M[EndCursor]                            ;Escreve numero na janela de texto
				MOV M[IO_CURSOR], R6
				ADD R3, '0'
				MOV M[IO], R3
				
				INC R6                            				;Atualiza Cursor
				MOV M[EndCursor], R6
			
				MOV R3, R0                                      ;Repoe flag
				DEC R4                                          ;Atualiza contador de algarismos do input
				JMP.NZ ccl_espera_inp							;Fecha ciclo
				ROR R2, 3                                       ;Repor ultima rotacao
				MOV M[EndIn], R2                                ;Guarda Input
				
				DSI
				JMP testa_input									;funcao principal compara input e gera output
cont_in:		MOV R3, M[EndOut]
                PUSH R3
                CALL escreve_out
				MOV R7, M[EndJog]
    			DEC R7
				MOV M[EndJog], R7
				PUSH R7
				CALL escreve_pontos
				MOV R7, M[EndJog]
				CMP R7, R0
				JMP.Z perdeu
				MOV R1, 1
				PUSH R1
				CALL print_linhas
				
				JMP inicio_jogada


				


                

;==================================================
;--------------------------------------------------
;		   PROCESSA A CHAVE GERADA
;--------------------------------------------------
;==================================================

trata_chave:	MOV R1, M[SP+2]
				XOR R1, randomizer
				AND R1, 0fffh
				MOV R4, R0
trtchv_ciclo:	PUSH R1
				AND R1, Masc_Ult3B
				CMP R1, 7
				BR.Z chave_dec
				CMP R1, R0
				BR.Z chave_inc
				POP R1
continua_chv:	ROR R1, 3
				INC R4
				CMP R4, 4
				BR.NZ trtchv_ciclo
				ROR R1,4
				MOV M[SP+3], R1
				RETN 1

chave_dec:		POP R1
				DEC R1
				BR continua_chv

chave_inc:		POP R1
				INC R1
				BR continua_chv
				
				
;==================================================
;--------------------------------------------------
;	   			COMPARADOR JOGADA
;--------------------------------------------------
;==================================================
				
testa_input:  	MOV R1, M[EndCh]
				MOV R2, M[EndIn]
				PUSH R0								;chave com valores desativados 
				PUSH R0								;input com valores desativados
				PUSH R0								;representacao do output	
				PUSH R1
				PUSH R2
				CALL testa_pos
				POP R3
				POP R2
				POP R1
				PUSH R0								;representacao do output	
				PUSH R1
				PUSH R2
				PUSH R3
				CALL testa_val
				POP R3
				MOV M[EndOut], R3
				JMP cont_in
				
				
;==================================================
;--------------------------------------------------
;	 COMPARA VALORES CERTOS NAS POSICOES CERTAS
;--------------------------------------------------
;==================================================				
				
				
				
testa_pos:		MOV R1, M[SP + 3]					;chave
				MOV R2, M[SP + 2]					;input
				MOV R3, R0							;output
				MOV R4, R0  						;contador de rotacoes
cmp_pos:		CMP R4, 4
				BR.NZ ccl_pos
				ROR R1, 4
				ROR R2, 4
				CMP R1, 0fffh
				JMP.Z ganhou
				MOV M[SP + 6], R1
				MOV M[SP + 5], R2
				MOV M[SP + 4], R3
				RETN 2
ccl_pos:		PUSH R1
				PUSH R2
				AND R1, Masc_Ult3B
				AND R2, Masc_Ult3B
				CMP R1, R2
				BR.Z pos_igual
				POP R2
				POP R1
rotate_pos:		ROR R1,3
				ROR R2,3
				INC R4
				BR cmp_pos

pos_igual:		POP R2
				POP R1
				OR R1, 7h
				OR R2, 7h
				INC R3
				BR rotate_pos
				
;==================================================
;--------------------------------------------------
;	COMPARA VALORES CERTOS NAS POSICOES ERRADAS
;--------------------------------------------------
;==================================================		
	
				
testa_val:		MOV R1, M[SP + 4]					;chave
				MOV R2, M[SP + 3]					;input
				MOV R3, M[SP + 2]					;output
				ROR R3, 4
				MOV R4, 4							;contador de rotacoes do input
				MOV R5, 4							;contador de rotacoes da chave
				
inicio_valor: 	CMP R5, R0
				JMP.Z next_in_val
				CMP R4, R0
				JMP.NZ salta_val
				ROL R3, 4
				MOV M[SP + 5], R3
				RETN 3
salta_val:		PUSH R2
				AND R2, Masc_Ult3B
				CMP R2, 7
				BR.Z next_valor
				PUSH R1
				AND R1, Masc_Ult3B
				CMP R1, R2
				BR.Z igual_valor
				POP R1
				ROR R1, 3
				DEC R5
				POP R2
				BR inicio_valor
				
next_valor:		POP R2
				ROR R2, 3
				DEC R4
reinicia_chv:	ROR R1, 3
				DEC R5
				CMP R5, R0
				BR.NZ reinicia_chv
				ROR R1, 4
				MOV R5, 4
				JMP inicio_valor
				
igual_valor:	POP R1
				POP R2
				INC R3
				OR R2, 7h
				OR R1, 7h
				ROR R2, 3
				DEC R4
				BR reinicia_chv

next_in_val:	ROR R2, 3
				DEC R4
				ROR R1, 4
				MOV R5, 4
				JMP inicio_valor





				
;==================================================
;--------------------------------------------------
;		  ESCREVE STRINGS TERMINADAS EM '!'
;--------------------------------------------------
;==================================================
				
print_str:		MOV R1, M [SP + 2]
				MOV R3, M[SP + 3]
				MOV R2, M[EndCursor]
				AND R2, FF00h
				OR R2, R3
ccl_prnt_str:	MOV M[IO_CURSOR], R2
				MOV R3, M[R1]
				CMP R3, fim_str_g
				BR.Z fim_str
				MOV M[IO], R3
				INC R1
				INC R2
				BR ccl_prnt_str
fim_str:		MOV M[EndCursor], R2
				RETN 2
				
				
				
;==================================================
;--------------------------------------------------
;		  		IMPRIME N LINHAS
;--------------------------------------------------
;==================================================				

print_linhas:	MOV R1, M[SP + 2]
				MOV R2, M[EndCursor]
				SHL R1, 8
				ADD R2,R1
				AND R2, FF00h
				OR R2, 3
				MOV M[EndCursor], R2
				RETN 1

				
				
;==================================================
;--------------------------------------------------
;		  	LIMPA ECRA E REINICIA CURSOR
;--------------------------------------------------
;==================================================					

cls:            MOV R1, R0
                MOV M[IO_CURSOR], R1
                CALL prnt_teto						;+-----------------------------------+
                CALL prnt_lados						;|									 |
                CALL prnt_teto						;+-----------------------------------+
                MOV R4, 20							;|									 |
prnt_corpo:     CALL prnt_lados						;|				...					 |
                DEC R4								;|									 |
                BR.NZ prnt_corpo					;|									 |
                CALL prnt_teto						;+-----------------------------------+

                MOV R1, R0
				OR R1, 0402h						;Inicializa cursor
                MOV M[IO_CURSOR], R1
                MOV M[EndCursor], R1
                RET

prnt_teto:      MOV R2, Traco
                MOV R3, Mais
                PUSH R3
                PUSH R2
                PUSH R1
                CALL prnt_ln_sqlt
                RET

prnt_lados:     MOV R2, Espaco
                MOV R3, Barra
                PUSH R3
                PUSH R2
                PUSH R1
                CALL prnt_ln_sqlt
                RET

prnt_ln_sqlt:   MOV R1, M[SP + 2]
                MOV R2, M[SP + 3]
                MOV R3, M[SP + 4]
                MOV M[IO_CURSOR], R1
                MOV M[IO], R3
                PUSH R1
prnt_dentro:    POP R1
                INC R1
                MOV M[IO_CURSOR], R1
                MOV M[IO], R2
                PUSH R1
                AND R1, FFh
                CMP R1, 76
                BR.NZ prnt_dentro
                POP R1
                MOV M[IO],R3
				ADD R1, 100h
				AND R1, FF00h
                MOV M[IO_CURSOR], R1
                RETN 3

;==================================================
;--------------------------------------------------
;		  ESCREVE OUTPUT NA JANELA DE TEXTO
;--------------------------------------------------
;==================================================	

escreve_out:    MOV R6, M[EndCursor]
                ADD R6, 8
                MOV M[IO_CURSOR], R6
                MOV R5, Xis
                MOV R1, 4                               ;Conta out escritos
                MOV R3, M[SP + 2]
                MOV R7, '_'
escreve_X:      PUSH R3
                AND R3, Fh
                CMP R3, R0
                BR.Z escreve_O
                POP R3
                DEC R3
                DEC R1                                  ;Nao compara R1 com 0 porque o caso de vitoria foi testado em testa_pos
                MOV M[IO], R5
                INC R6
                MOV M[IO_CURSOR], R6
                BR escreve_X

escreve_O:      POP R3
                ROR R3, 4
                MOV R4, 'O'
escreve_O_aux:  PUSH R3
                AND R3, Fh
                CMP R3, R0
                BR.Z escreve_traco
                POP R3
                DEC R3
                DEC R1
                MOV M[IO], R4
                INC R6
                MOV M[IO_CURSOR], R6
                BR escreve_O_aux

escreve_traco:  POP R3
escrv_trc_aux:  CMP R1, R0
                BR.Z break_out
                MOV M[IO], R7
                INC R6
                MOV M[IO_CURSOR], R6
                DEC R1
                BR escrv_trc_aux

break_out:      MOV M[EndCursor], R6
                RETN 1
			
				
;==================================================
;--------------------------------------------------
;			String de vitoria
;--------------------------------------------------
;==================================================	

ganhou:			MOV R6, M[EndCursor]
                ADD R6, 008h
                MOV M[IO_CURSOR], R6
                MOV R5, Xis
                MOV R1, 4                               
escreve_ganhou: MOV M[IO], R5
                INC R6
                MOV M[IO_CURSOR], R6
		        DEC R1
                BR.NZ escreve_ganhou
				MOV R1, 3
				PUSH R1
				CALL print_linhas
				MOV R1, str_ganhou
				MOV R2, 24
				PUSH R2
				PUSH R1
				CALL print_str
				MOV R7, M[EndJog]
				MOV R6, M[EndHScore]
				CMP R7, R6
				JMP.NP nao_HS
				MOV R1, 3
				PUSH R1
				CALL print_linhas
				MOV R1, M[EndJog]
				MOV M[EndHScore], R1
				PUSH R1
				CALL escreve_HS
nao_HS:			MOV R1, str_reinicia
				MOV R2, 28
				PUSH R2
				PUSH R1
				CALL print_str
				ENI
				MOV R7, INT_MASK_INI
				MOV M[INT_MASK_ADDR], R7
				MOV R1, R0
				JMP espera_IA


;==================================================
;--------------------------------------------------
;			String de derrota
;--------------------------------------------------
;==================================================	

perdeu_tempo:	MOV R7, M[EndCursor]
				ADD R7, 200h
				AND R7, FF00h
				MOV M[IO_CURSOR], R7
				MOV M[EndCursor], R7
				
				MOV R7, str_perdeu_tmp
				MOV R6, 2
				PUSH R6
				PUSH R7
				CALL print_str
				
				MOV R7, 1
				PUSH R7
				CALL print_linhas
				
				MOV R7, M[EndCursor]
				MOV M[IO_CURSOR], R7
				
				MOV R7, str_chave
				MOV R6, 2
				PUSH R6
				PUSH R7
				CALL print_str
				
				MOV R7, M[EndCursor]
				MOV M[IO_CURSOR], R7
				
				MOV R6, 4
				MOV R5, M[EndCh]
				PUSH R7
				PUSH R6
				PUSH R5
				CALL escreve_chv
				
				MOV R7, 2
				PUSH R7
				CALL print_linhas
				JMP reiniciar
				
perdeu:			MOV R1, 2
				PUSH R1
				CALL print_linhas
				
				MOV R2, str_perdeu1
				MOV R1, 2
				PUSH R1
				PUSH R2
				CALL print_str
				
				MOV R7, M[EndCursor]
				ADD R7, 100h
				AND R7, FF00h
				MOV M[EndCursor], R7
				MOV R7, str_chave
				MOV R6, 2
				PUSH R6
				PUSH R7
				CALL print_str
				
				MOV R6, 4
				MOV R5, M[EndCh]
				MOV R7, M[EndCursor]
				INC R7
				PUSH R7
				PUSH R6
				PUSH R5
				CALL escreve_chv
				
				MOV R1, 1
				PUSH R1
				CALL print_linhas
				
				MOV R2, str_perdeu2
				MOV R1, 2
				PUSH R1
				PUSH R2
				CALL print_str
				
reiniciar:		MOV R1, 2
				PUSH R1
				CALL print_linhas
				
				MOV R1, str_reinicia
				MOV R2, 28
				PUSH R2
				PUSH R1
				CALL print_str
				
				;ENI
				;MOV R7, INT_MASK_INI
				;MOV M[INT_MASK_ADDR], R7
				;MOV R1, R0						;Inicializar gerador de chave
				JMP espera_IA

escreve_chv:	MOV R5, M[SP+2]
				MOV R6, M[SP+3]
				MOV R7, M[SP+4]
				ROR R5, 9
escreveChv_ccl: INC R7
				MOV M[IO_CURSOR], R7
				PUSH R5
				AND R5, Masc_Ult3B
				ADD R5, '0'
				MOV M[IO], R5
				POP R5
				ROL R5, 3
				DEC R6
				BR.NZ escreveChv_ccl
				RETN 3

				
;==================================================
;--------------------------------------------------
;	Escreve pontuacao no 7 segment display
;--------------------------------------------------
;==================================================					

escreve_pontos:	MOV R6, M[SP + 2]
				CMP R6, 10
				BR.NN divide
				MOV M[PONT_ATUAL2], R0
				MOV M[PONT_ATUAL1], R6
				RETN 1
divide:			SUB R6, 10
				MOV M[PONT_ATUAL1], R6
				MOV R5, 1
				MOV M[PONT_ATUAL2], R5
				RETN 1

			
;==================================================
;--------------------------------------------------
;		Escreve melhor pontuacao no LCD
;--------------------------------------------------
;==================================================					
				
escreve_HS:		MOV R7, LCD_limpa_2
				MOV M[LCD_CONTROL], R7
				MOV R7, LCD_limpa_2
				MOV M[LCD_CONTROL], R7
				MOV R1, M[SP+2]
				CMP R1, 10
				BR.NN sup_dez
				ADD R1, '0'							;Converter para ASCII
				MOV R7, LCD_Prim_dig
				MOV M[LCD_CONTROL], R7
				MOV M[LCD_WRITE], R0
				MOV R7, LCD_Seg_dig
				MOV M[LCD_CONTROL], R7
				MOV M[LCD_WRITE], R1
				RETN 1
sup_dez:		SUB R1, 10
				ADD R1, '0'
				MOV R2, '1'
				MOV R7, LCD_Prim_dig
				MOV M[LCD_CONTROL], R7
				MOV M[LCD_WRITE], R2
				MOV R7, LCD_Seg_dig
				MOV M[LCD_CONTROL], R7
				MOV M[LCD_WRITE], R1
				RETN 1
				
				
				
			
