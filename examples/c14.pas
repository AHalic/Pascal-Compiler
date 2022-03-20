// exemplo de uso do if

program ifelseChecking;
var
   { local variable definition }
   a : integer;

begin
   a := 10;
   (* check the boolean condition *)
   if( a < 20 ) then
   begin
      (* if condition is true then print the following *)
      a := 4;
      a := a + 2;
   end
   
   else
      (* if condition is false then print the following *) 
      a := 4;
   writeln(a);
end.