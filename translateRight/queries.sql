-- Query 1
WITH local_anomalia_count(latitude, longitude, nome, anomalia_count)
AS (SELECT L.latitude, L.longitude, L.nome, COUNT(*)
    FROM (local_publico NATURAL JOIN item) AS L, incidencia AS I
    WHERE L.id = I.item_id
    GROUP BY (latitude, longitude))
SELECT latitude, longitude, nome
FROM local_anomalia_count
WHERE anomalia_count = (SELECT MAX(anomalia_count) FROM local_anomalia_count);


-- Query 2
WITH utilizador_regular_tmp(email, anomalia_count)
     AS (SELECT I.email, COUNT(A.id)
         FROM (utilizador_regular NATURAL JOIN incidencia) AS I, anomalia A
         WHERE I.anomalia_id = A.id
               AND A.tem_anomalia_redacao = false
               AND TIMESTAMP '2019-01-01' <= A.ts
               AND A.ts < TIMESTAMP '2019-07-01'
         GROUP BY I.email)
SELECT email
FROM utilizador_regular_tmp
WHERE anomalia_count = (SELECT MAX(anomalia_count) FROM utilizador_regular_tmp);


-- Query 3
WITH norte_rio_maior AS (SELECT * FROM local_publico
                         WHERE latitude > 39.336775),
     utilizador_local(email, latitude, longitude)
       AS (SELECT I.email, LP.latitude, LP.longitude
           FROM incidencia AS I, anomalia A,
                (item NATURAL JOIN local_publico) AS LP
           WHERE I.item_id = LP.id
                 AND I.anomalia_id = A.id
                 AND extract(year FROM A.ts) = 2019)
SELECT distinct email
FROM utilizador_local U1
WHERE NOT EXISTS (SELECT latitude, longitude FROM norte_rio_maior
                  EXCEPT
                  SELECT latitude, longitude FROM utilizador_local U2
                  WHERE U1.email = U2.email);


-- Query 4
WITH incidencia_qual_sul(email, anomalia_id)
       AS (SELECT I.email, I.anomalia_id
           FROM (utilizador_qualificado NATURAL JOIN incidencia) AS I,
                (item NATURAL JOIN local_publico) AS LP, anomalia A
           WHERE I.item_id = LP.id
                 AND I.anomalia_id = A.id
                 AND LP.latitude < 39.336775
                 AND extract(year FROM A.ts) = extract(year FROM current_date))
SELECT distinct U1.email
FROM incidencia_qual_sul U1
WHERE EXISTS (SELECT U2.anomalia_id
              FROM incidencia_qual_sul U2
              WHERE U2.email = U1.email
              EXCEPT
              SELECT C.anomalia_id
              FROM (proposta_de_correcao NATURAL JOIN correcao) AS C
              WHERE C.email = U1.email);
