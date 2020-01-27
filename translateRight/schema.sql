drop table local_publico cascade;
drop table item cascade;
drop table anomalia cascade;
drop table anomalia_traducao cascade;
drop table duplicado cascade;
drop table utilizador cascade;
drop table utilizador_qualificado cascade;
drop table utilizador_regular cascade;
drop table incidencia cascade;
drop table proposta_de_correcao cascade;
drop table correcao cascade;

create table local_publico (
    latitude      numeric(8, 6) not null,
    longitude     numeric(9, 6) not null,
    nome          varchar(255) not null,
    constraint pk_local_publico primary key(latitude, longitude),
    constraint latitude_check check (-90 <= latitude and latitude <= 90),
    constraint longitude_check check (-180 <= longitude and longitude <= 180)
);

create table item (
    id             serial,
    descricao      varchar(255) not null,
    localizacao    varchar(255) not null,    -- not sure about the type
    latitude       numeric(8, 6) not null,
    longitude      numeric(9, 6) not null,
    constraint fk_item_local_publico foreign key(latitude, longitude)
               references local_publico(latitude, longitude) on delete cascade,
    constraint pk_item primary key(id)
);

create table anomalia (
    id             serial,
    zona           box not null,
    imagem         bytea not null,
    lingua         varchar(255) not null,
    ts             timestamp not null,
    descricao      varchar(255) not null,
    tem_anomalia_redacao boolean not null,
    constraint pk_anomalia primary key(id)
);

create table anomalia_traducao (
    id             integer not null,
    zona2          box not null,
    lingua2        varchar(255) not null,
    constraint pk_anomalia_traducao primary key(id),
    constraint fk_anomalia_traducao_anomalia foreign key(id)
                references anomalia(id) on delete cascade
);


create table duplicado (
    item1          integer not null,
    item2          integer not null,
    constraint pk_duplicado primary key(item1, item2),
    constraint fk_duplicado_item foreign key(item1) 
                references item(id) on delete cascade,
    constraint fk_duplicado_item2 foreign key(item2) 
                references item(id) on delete cascade,                    --neds to be like this, because it's different instances of an item.id
    check(item1 < item2)
);

create table utilizador (
    email          varchar(255) not null,
    password       varchar(255) not null,
    constraint pk_utilizador primary key(email)         
);

--TODO: how to implement RI-4?

create table utilizador_qualificado (
    email           varchar(255) not null,
    constraint pk_utilizador_qualificado primary key(email),
    constraint fk_utilizador_qualificado_utilizador foreign key(email)
                references utilizador(email)
);


--TODO: how to implement RI-5?

create table utilizador_regular (
    email           varchar(255) not null,
    constraint pk_utilizador_regular primary key(email),
    constraint fk_utilizador_regular_utilizador foreign key(email)
                references utilizador(email)
);

--TODO: how to implement RI-6?

create table incidencia (
    anomalia_id        integer not null,
    item_id            integer not null,
    email              varchar(255) not null,
    constraint pk_incidencia primary key(anomalia_id),
    constraint fk_incidencia_anomalia foreign key(anomalia_id)
                references anomalia(id) on delete cascade,
    constraint fk_incidencia_item foreign key(item_id)
                references item(id) on delete cascade,
    constraint fk_incidencia_utilizador foreign key(email)
                references utilizador(email)
);


create table proposta_de_correcao (
    email              varchar(255) not null,
    nro                integer not null,
    data_hora          timestamp not null,                  --Is it Timestamp?
    texto              text not null,
    constraint pk_proposta_de_correcao primary key(email, nro),
    constraint fk_proposta_email foreign key(email)
                 references utilizador_qualificado(email)
);

create table correcao (
    email              varchar(255) not null,
    nro                integer not null,
    anomalia_id        integer not null,
    constraint pk_correcao primary key(email, nro, anomalia_id),
    constraint fk_correcao_proposta foreign key(email, nro)
                references proposta_de_correcao(email, nro) on delete cascade,
    constraint fk_correcao_incidencia foreign key(anomalia_id)
                references incidencia(anomalia_id) on delete cascade
);


