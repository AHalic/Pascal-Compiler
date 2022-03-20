program FatorialPD;

var
  n, x, Contador, fatorial: integer;
  resultados: array [0..100] of integer;
begin
  //
  n := 0;

  while (n < 100) do
    begin;
      resultados[n] := 0;
      n := n + 1;
    end;

  // 
  writeln('');
  writeln('           Calculo do fatorial de um numero');
  writeln('');

  while (true) do
    begin
      write('Entre com um inteiro nao-negativo: ');
      read(n);
    
      fatorial := 1;
      Contador := 1;

      while (n >= Contador) do
        begin
          if (resultados[n] = 0) then
            begin
              resultados[Contador] := fatorial*Contador;
              fatorial := resultados[Contador];
              Contador := Contador+1;
              writeln('Calculando!');
            end
          else
            begin
              fatorial := resultados[n];
              Contador := n + 1;
              writeln('Já Calculado!');
            end;
        end;

      write ('O valor de ');
      write(n);
      write('!: ');
      writeln(fatorial);
  end;
end.
