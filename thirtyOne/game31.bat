@echo off
title MENU PRINCIPAL
color 0f
:Menu principal
cls
echo.
echo Menu
echo ----
echo.
echo 1) Jogar
echo 2) Regras
echo 3) Sair
echo.
set /p decisao=
if %decisao%== 1 goto adversario
if %decisao%== 2 goto regras
if %decisao%== 3 exit
cls
echo Valor invalido. Por favor inserir 1, 2 ou 3.
pause
goto Menu principal
pause
:adversario
title MODOS DE JOGO
cls
echo.
echo.
echo 1) 1 Jogador
echo 2) 2 Jogadores
echo 3) Voltar
echo.
set /p escolha=
if %escolha%== 1 goto Contra PC
if %escolha%== 2 goto Multiplayer
if %escolha%== 3 goto Menu Principal
cls
echo Valor invalido. Por favor inserir 1, 2 ou 3.
pause
goto adversario
:Multiplayer
title 2 JOGADORES
cls
echo.
echo Par ou impar?
pause
cls
echo Foi gerado um numero:
echo %random%
echo.
echo Se for par, comeca quem disse par,
echo Caso contrario...
pause
cls
echo Bem, voces sabem...
pause
goto usernames
:usernames
title NAMES, PLEASE...
cls
set pl1=Jogador 1
set pl2=Jogador 2
set /p pl1=Inserir Nome do jogador 1: 
set /p pl2=Inserir Nome do jogador 2: 
pause
cls
echo Bem vindos, %pl1% e %pl2%.
pause
cls
echo O JOGO VAI COMECAR EM
ping localhost -n 2 >nul
cls
echo.
echo    3
ping localhost -n 2 >nul
cls
echo.
echo    2
ping localhost -n 2 >nul
cls
echo.
echo    1
ping localhost -n 2 >nul
cls
echo.
echo Boa Sorte
pause
goto jogo1v1
:jogo1v1
title Jogo do 31
color 02
:0
cls
echo.
echo   0
echo.
set /p n=
if %n%== 1 goto 1
if %n%== 2 goto 2
if %n%== 3 goto 3
cls
echo O valor introduzido e invalido
pause
goto 0
:1
cls
set wiohvg= 1
echo.
echo   %wiohvg%
set /p a=
if %a%== 2 goto 2
if %a%== 3 goto 3
if %a%== 4 goto 4
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:2
cls
set wiohvg= 2
echo.
echo   %wiohvg%
set /p b=
if %b%== 3 goto 3
if %b%== 4 goto 4
if %b%== 5 goto 5
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:3
cls
set wiohvg= 3
echo.
echo   %wiohvg%
set /p c=
if %c%== 4 goto 4
if %c%== 5 goto 5
if %c%== 6 goto 6
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:4
cls
set wiohvg= 4
echo.
echo   %wiohvg%
set /p d=
if %d%== 5 goto 5
if %d%== 6 goto 6
if %d%== 7 goto 7
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:5
cls
set wiohvg= 5
echo.
echo   %wiohvg%
set /p e=
if %e%== 6 goto 6
if %e%== 7 goto 7
if %e%== 8 goto 8
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:6
cls
set wiohvg= 6
echo.
echo   %wiohvg%
set /p f=
if %f%== 7 goto 7
if %f%== 8 goto 8
if %f%== 9 goto 9
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:7
cls
set wiohvg= 7
echo.
echo   %wiohvg%
set /p g=
if %g%== 8 goto 8
if %g%== 9 goto 9
if %g%== 10 goto 10
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:8
cls
set wiohvg= 8
echo.
echo   %wiohvg%
set /p h=
if %h%== 9 goto 9
if %h%== 10 goto 10
if %h%== 11 goto 11
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:9
cls
set wiohvg= 9
echo.
echo   %wiohvg%
set /p i=
if %i%== 10 goto 10
if %i%== 11 goto 11
if %i%== 12 goto 12
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:10
cls
set wiohvg= 10
echo.
echo   %wiohvg%
set /p j=
if %j%== 11 goto 11
if %j%== 12 goto 12
if %j%== 13 goto 13
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:11
cls
set wiohvg= 11
echo.
echo   %wiohvg%
set /p k=
if %k%== 12 goto 12
if %k%== 13 goto 13
if %k%== 14 goto 14
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:12
cls
set wiohvg= 12
echo.
echo   %wiohvg%
set /p l=
if %l%== 13 goto 13
if %l%== 14 goto 14
if %l%== 15 goto 15
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:13
cls
set wiohvg= 13
echo.
echo   %wiohvg%
set /p m=
if %m%== 14 goto 14
if %m%== 15 goto 15
if %m%== 16 goto 16
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:14
cls
set wiohvg= 14
echo.
echo   %wiohvg%
set /p n=
if %n%== 15 goto 15
if %n%== 16 goto 16
if %n%== 17 goto 17
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:15
cls
set wiohvg= 15
echo.
echo   %wiohvg%
set /p o=
if %o%== 16 goto 16
if %o%== 17 goto 17
if %o%== 18 goto 18
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:16
cls
set wiohvg= 16
echo.
echo   %wiohvg%
set /p p=
if %p%== 17 goto 17
if %p%== 18 goto 18
if %p%== 19 goto 19
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:17
cls
set wiohvg= 17
echo.
echo   %wiohvg%
set /p q=
if %q%== 18 goto 18
if %q%== 19 goto 19
if %q%== 20 goto 20
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:18
cls
set wiohvg= 18
echo.
echo   %wiohvg%
set /p r=
if %r%== 19 goto 19
if %r%== 20 goto 20
if %r%== 21 goto 21
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:19
cls
set wiohvg= 19
echo.
echo   %wiohvg%
set /p s=
if %s%== 20 goto 20
if %s%== 21 goto 21
if %s%== 22 goto 22
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:20
cls
set wiohvg= 20
echo.
echo   %wiohvg%
set /p t=
if %t%== 21 goto 21
if %t%== 22 goto 22
if %t%== 23 goto 23
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:21
cls
set wiohvg= 21
echo.
echo   %wiohvg%
set /p u=
if %u%== 22 goto 22
if %u%== 23 goto 23
if %u%== 24 goto 24
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:22
cls
set wiohvg= 22
echo.
echo   %wiohvg%
set /p v=
if %v%== 23 goto 23
if %v%== 24 goto 24
if %v%== 25 goto 25
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:23
cls
set wiohvg= 23
echo.
echo   %wiohvg%
set /p w=
if %w%== 24 goto 24
if %w%== 25 goto 25
if %w%== 26 goto 26
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:24
cls
set wiohvg= 24
echo.
echo   %wiohvg%
set /p x=
if %x%== 25 goto 25
if %x%== 26 goto 26
if %x%== 27 goto 27
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:25
cls
set wiohvg= 25
echo.
echo   %wiohvg%
set /p y=
if %y%== 26 goto 26
if %y%== 27 goto 27
if %y%== 28 goto 28
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:26
cls
set wiohvg= 26
echo.
echo   %wiohvg%
set /p z=
if %z%== 27 goto 27
if %z%== 28 goto 28
if %z%== 29 goto 29
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:27
cls
set wiohvg= 27
echo.
echo   %wiohvg%
set /p aa=
if %aa%== 28 goto 28
if %aa%== 29 goto 29
if %aa%== 30 goto 30
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:28
cls
set wiohvg= 28
echo.
echo   %wiohvg%
set /p ab=
if %ab%== 29 goto 29
if %ab%== 30 goto 30
if %ab%== 31 goto 31
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:29
cls
set wiohvg= 29
echo.
echo   %wiohvg%
set /p ac=
if %ac%== 30 goto 30
if %ac%== 31 goto 31
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:30
cls
set wiohvg= 30
echo.
echo   %wiohvg%
set /p ad=
if %ad%== 31 goto 31
cls
echo O valor introduzido e invalido
pause
goto %wiohvg%
:31
cls
echo.
echo FIM DO JOGO
echo VITORIA
echo PARABENS
pause
goto Menu Principal
:Contra PC
title 1 JOGADOR
cls
echo.
echo 1) Facil
echo 2) Medio
echo 3) Impossivel
echo 4) Voltar
echo.
set /p dificuldade=Escolher dificuldade: 
if %dificuldade%== 1 goto facil
if %dificuldade%== 2 goto medio
if %dificuldade%== 3 goto impossivel
if %dificuldade%== 4 goto adversario
echo Valor invalido. Por favor inserir 1, 2, 3 ou 4.
pause
goto Contra PC
:regras
title REGRAS
cls
echo.
echo.
echo 1 O primeiro a dizer 31 ganha
echo 2 Comeca-se a contar desde o 0 e vai-se somando 1, 2 ou 3
echo 3 E estritamente OBRIGATORIA a diversao
pause
goto Menu Principal
pause
goto Menu Principal