.class public Names
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
    .limit stack 65535
    .limit locals 12

    ; STR TABLE
    0: ldc "Maria"
    1: astore 7
    2: ldc "Joao"
    3: astore 8
    4: ldc "Pedro"
    5: astore 9
    6: ldc " say hello to "
    7: astore 10
    ; END STR TABLE


    8: aload 7
    9: astore 0
    10: aload 8
    11: astore 1
    12: aload 9
    13: astore 2
    14: getstatic java/lang/System/out Ljava/io/PrintStream;
    15: aload 0
    16: invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    17: getstatic java/lang/System/out Ljava/io/PrintStream;
    18: aload 10
    19: invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    20: getstatic java/lang/System/out Ljava/io/PrintStream;
    21: aload 1
    22: invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    23: getstatic java/lang/System/out Ljava/io/PrintStream;
    24: ldc "\n"
    25: invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    26: return
.end method
