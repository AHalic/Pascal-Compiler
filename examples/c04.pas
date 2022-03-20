program Flags;

var
    flag : boolean;

begin
    flag := 3.0 < 2;
    writeln(flag);

    flag := 1.0 > 1;
    writeln(flag);

    flag := 1.0 >= 1.0;
    writeln(flag);

    flag := 1.0 <> 5.0;
    writeln(flag);

    flag := 5.1 <= 5.0;
    writeln(flag);

    flag := 5.0 = 5.0;
    writeln(flag);
end.
