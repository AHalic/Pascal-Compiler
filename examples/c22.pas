program FunctionWithVars;

var
    a: integer;
begin
    a := 0;

    while (true) do
    begin
        read(a);

        if (a <= 10) then
            break;
    end;
end.
