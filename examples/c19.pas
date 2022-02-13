// exemplo utilizando else if
program ifelse_ifelseChecking;

var
   { local variable definition }
   a : integer;
   b: integer;

begin
   a := 100;
   (* check the boolean condition *)
   if (a = 10)  then
    begin
        a := 2;
        b := a + 3;
    end
   else if ( a = 20 ) then
        b := 3
   else if( a = 30 ) then 
        b := 1
   else
        b := 0;

end.