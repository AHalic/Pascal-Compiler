program posneg;
uses crt;
var 
 no : integer;
 do: string; (* Uso de palavra reservada*)
begin
 clrscr;
  writeln('Enter a number:');
  readln(no);

  if (no > 0) then
   writeln('You enter Positive Number')
  else
    if (no < 0) then
     writeln('You enter Negative number')
    else
      if (no = 0) then
      writeln('You enter Zero');

  readln;
 end.