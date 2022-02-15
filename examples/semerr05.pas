// exemplo de uso do if

program ifelseChecking;
var
   { local variable definition }
   a : integer;

begin
   a := 10;
   (* check the boolean condition *)
   if( a + 20 ) then
   begin
      a := 4;
      a := a + 2;
    end
   
   else if (a < 10) then
   begin
      a := 4;
   end
   
   else if (a < 10) then
   begin
      a := 4;
   end

   else
      writeln('Nenhum')

end.