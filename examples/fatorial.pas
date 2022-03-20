program Fatorial;

var
  n, x, Contador, fatorial: integer;
begin
  writeln('');
  writeln('           Calculo do fatorial de um numero');
  writeln('');

  write('Entre com um inteiro nao-negativo: ');
  read(n);
 
  fatorial := 1;
  Contador := 1;

  while (n >= Contador) do
    begin
    fatorial := fatorial*Contador;
    Contador := Contador+1;
    end;

  write ('O valor de ');
  write(n);
  write('!: ');
  writeln(fatorial);
end.
