program Operators;

var
    number0: integer;
    number1: real;
    flag: real;
    number : real;

begin
    number0 := 1;
    write(number0);
    number1 := 3 - -2.0;
    write(number1);
    number1 := number0 + number1;
    write(number1);

    flag := (1.0 + (3 + 5));
    write(flag);

    number := 10.0 * 2.0;
    write(number);
    number := 10.0/2.0;
    write(number);
    number := 3/1.0;
    write(number);

    //number1 := 2.1;
    //number1 := -3.0;


end.
