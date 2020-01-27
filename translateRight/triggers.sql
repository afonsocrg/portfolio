-- RI-1
create or replace function
check_overlap_proc() returns trigger 
as $$
    declare
        zona1   box;
    begin
        select zona into zona1
        from anomalia where id=new.id;

        if zona1 && new.zona2 then
            raise exception
            'As zonas da anomalia % não se podem intersetar.', new.id;
        end if;
        return new;
    end;
$$ language plpgsql;
create trigger check_overlap
after insert on anomalia_traducao 
for each row execute procedure check_overlap_proc();

-- RI-2
create or replace function
check_languages() returns trigger
as $$
    declare
        lingua1 VARCHAR(255);
    begin
        select lingua into lingua1
        from anomalia where id = new.id;

        if lingua1 = new.lingua2 then
            raise exception
            'As linguas da anomalia % não podem ser iguais.', new.id;
        end if;
        return new;
    end;
$$ language plpgsql;

create trigger check_lang
after insert on anomalia_traducao
for each row execute procedure check_languages();

-- RI-4
-- TODO: tambem esta a fazer verificacoes dos RI5 e RI6. ver com professor se podemos deixar assim
create or replace function
check_completeness_proc() returns trigger
as $$
    declare 
        inRegular   boolean;
        inQualified boolean;
    begin
        select exists(
            select 1
            from utilizador_regular
            where email = new.email
        ) into inRegular;
        
        select exists(
            select 1
            from utilizador_qualificado
            where email = new.emai
        ) into inQualified;

        if inRegular and inQualified then
            raise exception
                'Utilizador % não pode estar em utilizador_regular e
                utilizador qualificado simultaneamente', new.email;
        elseif (not inRegular and not inQualified) then
            raise exception
                'Utilizador % tem de estar em utilizador_regular
                ou utilizador_qualificado', new.email;
        end if;
        return new;
    end;
$$ language plpgsql;
create constraint trigger check_completeness
after insert on utilizador
deferrable initially deferred
for each row execute procedure check_completeness_proc();


-- RI-5
create or replace function
check_user_qualificado_proc() returns trigger
as $$
    declare
        inRegular boolean;
    begin
        select exists(
            select 1
            from utilizador_regular
            where email = new.emai
        ) into inRegular;

        if inRegular then
            raise exception
            'Utilizador % já está em utilizador_regular', new.email;
        end if;
        return new;
    end;
$$ language plpgsql;
create trigger check_user_qualificado
after insert on utilizador_qualificado
for each row execute procedure check_user_qualificado_proc();


--RI-6
create or replace function
check_user_regular_proc() returns trigger
as $$
    declare
        inQualified boolean;
    begin
        select exists(
            select 1
            from utilizador_qualificado
            where email = new.emai
        ) into inQualified;

        if inQualified then
            raise exception
            'Utilizador % já está em utilizador_qualificado', new.email;
        end if;
        return new;
    end;
$$ language plpgsql;
create trigger check_user_regular
after insert on utilizador_regular
for each row execute procedure check_user_regular_proc();
