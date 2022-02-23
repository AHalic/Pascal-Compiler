program TypeErrorProgram;

var
    a: integer;
    b: real;
    nome: string;

begin
    a := 42;
    b := 42.0;
    nome := a; // string and int error
    nome := b; // string and real error
end.
