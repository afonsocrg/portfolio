--------------------------------------------------------------------
--                      LOCAIS_PUBLICOS                           --
--              Rio Maior, 2 a norte e 2 a sul                    --
--------------------------------------------------------------------

insert into local_publico values(39.336775, -8.936379, 'Cafe de Rio Maior');
insert into local_publico values(40.203314, -8.410257, 'Estadio Coimbra');
insert into local_publico values(41.413055, -8.512330, 'Restaurante de Famalicao');
insert into local_publico values(38.728573, -9.137904, 'Cais de Lisboa');
insert into local_publico values(31.034113, -7.927548, 'Centro de Faro');

--------------------------------------------------------------------
--                           ITEM                                 --
-- 1 em todos os locais, exceto Lisboa, Coimbra e Faro que tem 2  --
--------------------------------------------------------------------

insert into item (descricao, localizacao, latitude, longitude) values('Item_RioMaior', 'Cafe do Abilio', 39.336775, -8.936379);
insert into item (descricao, localizacao, latitude, longitude) values('Item_Coimbra_1', 'Estadio Academica porta 1', 40.203314, -8.410257);
insert into item (descricao, localizacao, latitude, longitude) values('Item_Coimbra_2', 'Estadio Academica porta 1', 40.203314, -8.410257); --Item duplicado
insert into item (descricao, localizacao, latitude, longitude) values('Item_Famalicão', 'Restaurante Ze do Pipo', 41.413055, -8.512330);
insert into item (descricao, localizacao, latitude, longitude) values('Item_Lisboa_1', 'Cafe do Cais do Sodre', 38.728573, -9.137904);
insert into item (descricao, localizacao, latitude, longitude) values('Item_Faro_1', 'Centro Comercial Faro', 31.034113, -7.927548);
insert into item (descricao, localizacao, latitude, longitude) values('Item_Lisboa_2', 'Cafe do Cais', 38.728573, -9.137904);
insert into item (descricao, localizacao, latitude, longitude) values('Item_Faro_2', 'Praça de Faro', 31.034113, -7.927548);

--------------------------------------------------------------------
--                          ANOMALIA                              --
--3 em Lisboa, Faro e Coimbra, 1 no resto                         --
-- Lisboa, Faro, Coimbra:                                         --
--      2 de tradução, 1 de redação                               --
-- Restantes:                                                     --
--      Alternar                                                  --
--------------------------------------------------------------------

-- Lisboa
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(0,0),(1,1)', '\xF07001', 'Portugues', '2019-04-23 19:59:00', 'Mau pronome', false);
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(0,0),(1,1)','\xF07002', 'Ingles', '2019-09-23 00:00:00', 'Mau adjetivo', false);
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(4,4),(5,5)','\xF07003', 'Frances', '2019-01-23 03:59:00', 'Mau verbo', true);
--Coimbra 
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(0,0),(1,1)','\xF07004', 'Grego', '2019-04-23 19:59:00', 'Mau nome', false);
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(0,0),(1,1)','\xF07004', 'Grego', '2019-04-23 19:59:00', 'Mau nome', false); -- Para item duplicado
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(4,4),(5,5)','\xF07005', 'Frances', '2019-04-23 19:59:00', 'Mau acento', true);
--Faro
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(0,0),(1,1)','\xF07006', 'Espanhol', '2019-07-23 19:59:00', 'Mau uso de maiscula', false);
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(2,2),(3,3)','\xF07007', 'Italiano', '2019-02-23 00:00:00', 'Ma frase', false);
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(4,4),(5,5)','\xF07008', 'Portugues', '2019-11-23 03:59:00', 'Mau nome', true);
--Famalicao
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(0,0),(1,1)','\xF07009', 'Romeno', '2019-05-23 19:59:00', 'Mau uso de maiscula', false);
--Rio Maio
insert into anomalia (zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) values('(0,0),(1,1)','\xF07010', 'Sueco', '2019-08-23 19:59:00', 'Mau uso de adverbio', true);

--------------------------------------------------------------------
--                     ANOMALIA TRADUCAO                          --
--------------------------------------------------------------------

insert into anomalia_traducao values(1, '((2, 2), (3, 3))', 'Frances');
insert into anomalia_traducao values(2, '((2, 2), (3, 3))', 'Portugues');

insert into anomalia_traducao values(4, '((2, 2), (3, 3))', 'Ingles');
insert into anomalia_traducao values(5, '((2, 2), (3, 3))', 'Ingles');

insert into anomalia_traducao values(7, '((2, 2), (3, 3))', 'Portugues');
insert into anomalia_traducao values(8, '((4, 4), (5, 5))', 'Frances');

insert into anomalia_traducao values(10, '((2, 2), (3, 3))', 'Portugues');

---------------------------------------------------------------------
--                          DUPLICADO                              --
--          Usar items 2 e 3 com anomalias 4 e 5                   --
---------------------------------------------------------------------

insert into duplicado values(2, 3);

---------------------------------------------------------------------
--                          UTILIZADOR                             --
--         6 utilizadores (3 qualificados, e regulares)            --
---------------------------------------------------------------------

insert into utilizador values('marcelo@xpto.com', 'S3cur3_PassW0rd');
insert into utilizador values('afonso@cpleic.com', '1_L0v3_Cp1usp1us');
insert into utilizador values('daniel@grupo23.com', '3sc0teir0_C0m_O');
insert into utilizador values('ze@companhia.com', '1234');
insert into utilizador values('to_ze@companhia.com', '4321');
insert into utilizador values('joao@companhia.com', '12345');

----------------------------------------------------------------------
--                   UTILIZADOR QUALIFICADO                       ----
----------------------------------------------------------------------

insert into utilizador_qualificado values('marcelo@xpto.com');
insert into utilizador_qualificado values('afonso@cpleic.com');
insert into utilizador_qualificado values('daniel@grupo23.com');

----------------------------------------------------------------------
--                     UTILIZADOR REGULAR                           --
----------------------------------------------------------------------

insert into utilizador_regular values('ze@companhia.com');
insert into utilizador_regular values('to_ze@companhia.com');
insert into utilizador_regular values('joao@companhia.com');

----------------------------------------------------------------------
--                         INCIDENCIA                               --
-- Marcelo-regista incidencias em todos os locais acima de Rio Maior--
----------------------------------------------------------------------

--Coimbra
insert into incidencia values(4, 2, 'marcelo@xpto.com');
insert into incidencia values(5, 3, 'ze@companhia.com');
insert into incidencia values(6, 2, 'afonso@cpleic.com');

-- Famalicao
insert into incidencia values(10, 4, 'marcelo@xpto.com');

--Rio Maior
insert into incidencia values(11, 1, 'daniel@grupo23.com');

--Lisboa
insert into incidencia values(1, 5, 'afonso@cpleic.com');
insert into incidencia values(2, 7, 'joao@companhia.com');
insert into incidencia values(3, 5, 'daniel@grupo23.com');

--Faro
insert into incidencia values(7, 6, 'afonso@cpleic.com');
insert into incidencia values(8, 8, 'ze@companhia.com');
insert into incidencia values(9, 6, 'daniel@grupo23.com');


----------------------------------------------------------------------
--                     PROPOSTA DE CORRECAO                         --
----------------------------------------------------------------------

insert into proposta_de_correcao values('marcelo@xpto.com', 1, '2019-11-30 00:00:00', 'Corrigido');
insert into proposta_de_correcao values('daniel@grupo23.com', 1, '2019-10-31 23:59:00', 'Correcao');
insert into proposta_de_correcao values('afonso@cpleic.com', 1, '2019-09-30 11:00:00', 'Corrigido');
insert into proposta_de_correcao values('marcelo@xpto.com', 2, '2019-11-30 00:00:00', 'Corrigido');
insert into proposta_de_correcao values('daniel@grupo23.com', 2, '2019-10-31 23:59:00', 'Correcao');
insert into proposta_de_correcao values('afonso@cpleic.com', 2, '2019-09-30 11:00:00', 'Corrigido');
insert into proposta_de_correcao values('marcelo@xpto.com', 3, '2019-11-30 00:00:00', 'Corrigido');
insert into proposta_de_correcao values('daniel@grupo23.com', 3, '2019-10-31 23:59:00', 'Correcao');

------------------------------------------------------------------------
--                           CORRECAO                                 --
-- Afonso não regista para todas as anomalias dele a sul de Rio Maior --
------------------------------------------------------------------------

insert into correcao values('afonso@cpleic.com', 1, 1);
insert into correcao values('afonso@cpleic.com', 2, 6);

insert into correcao values('marcelo@xpto.com', 1, 7);
insert into correcao values('marcelo@xpto.com', 2, 2);
insert into correcao values('marcelo@xpto.com', 3, 11);

insert into correcao values('daniel@grupo23.com', 1, 3);
insert into correcao values('daniel@grupo23.com', 2, 10);
insert into correcao values('daniel@grupo23.com', 3, 9);


