program Flags;

var
    flag1 : boolean;
    flag2 : boolean;

begin
    flag2 := (TRUE AND FALSE) OR (FALSE OR TRUE);
    write(flag2);
end.
