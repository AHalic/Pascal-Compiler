// Exemplo de programa usando loop aninhado sem block no externo

program whileLoop;
var
   a: integer;

begin
   a := 10;
   while  a < 20  do
    while a < 30 do
        begin
            a := a + 2;
        end;
   a := a + 1;
end.