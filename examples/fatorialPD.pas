program FatorialPD;

var
  n, contador: integer;
  // Não é possível armazenar fatoriais acima de 12 em inteiros
  fatoriais: array [0..12] of integer;
begin
  // Define os fatoriais de 0 e 1 como 1
  fatoriais[0] := 1;
  fatoriais[1] := 1;

  // Calcula os fatoriais de 2 até 12
  n := 2;

  while (n <= 12) do
    begin;
      fatoriais[n] := n*fatoriais[n - 1];
      n := n + 1;
    end;

  writeln('Digite um valor inteiro entre 0 e 12::')
  // Lê o primeiro n
  read(n);

  // Repete enquanto o valor de n for válido
  while ((n >= 0) AND (n <= 12)) do
    begin;
      // Escreve o fatorial de n
      write(n);
      write('! = ');
      writeln(fatoriais[n]);

      // Lê o próximo n
      writeln('Digite outro valor inteiro entre 0 e 12:')
      read(n);
    end;

  // Imprime o motivo da quebra do loop
  if (n <= 0) then
      writeln('Número negativo!')
  else
      writeln('Não é possível calcular fatoriais acima de 12!')
end.
