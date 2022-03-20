// exemplo de uso do if

program ifelseChecking;
var
   { local variable definition }
   a : integer;

begin
   a := 21;
   (* check the boolean condition *)
   if( a < 20 ) then
   begin
      (* if condition is true then print the following *)
      a := 4;
      a := a + 2;
   end;
   
   writeln(a);
end.