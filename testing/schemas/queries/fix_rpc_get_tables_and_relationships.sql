-- Fix the function to ensure it fetches relationships correctly
create or replace function public.get_tables_and_relationships()
returns jsonb
language plpgsql
as $$
declare
    result jsonb;
begin
    -- Fetch tables
    result := jsonb_build_object(
        'tables', jsonb_agg(jsonb_build_object('table_name', table_name))
    ) from (
        select table_name
        from information_schema.tables
        where table_schema = 'public'
    ) t;

    -- Fetch relationships
    result := jsonb_set(
        result,
        '{relationships}',
        to_jsonb(array(
            select jsonb_build_object(
                'table_name', kcu.table_name,
                'foreign_table_name', ccu.table_name
            )
            from information_schema.key_column_usage kcu
            join information_schema.referential_constraints rc
                on kcu.constraint_name = rc.constraint_name
            join information_schema.constraint_column_usage ccu
                on rc.unique_constraint_name = ccu.constraint_name
            where kcu.table_schema = 'public'
        ))
    );

    return result;
end;
$$;

-- Grant execution privileges to the API role
grant execute on function public.get_tables_and_relationships() to anon, authenticated;