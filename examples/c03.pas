program Operators;

var
    number0: integer;
    number1: real;
    flag: real;
    number : real;

begin
    number0 := 1;
    writeln(number0);
    number1 := 3 - -2.0;
    writeln(number1);
    number1 := number0 + number1;
    writeln(number1);

    flag := (1.0 + (3 + 5));
    writeln(flag);

    number := 10.0 * 2.0;
    writeln(number);
    number := 10.0/2.0;
    writeln(number);
    number := 3/1.0;
    writeln(number);

    //number1 := 2.1;
    //number1 := -3.0;


end.
