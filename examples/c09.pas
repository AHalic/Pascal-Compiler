program Flags;

var
    flag1 : boolean;
    flag2 : boolean;

begin
    flag2 := (TRUE AND FALSE) OR (FALSE OR TRUE);
    writeln(flag2);
end.
