.class public FunctionWithVars
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
    .limit stack 65535
    .limit locals 2

    ; STR TABLE
    ; END STR TABLE


    0: ldc 1
    1: istore 0
    2: getstatic java/lang/System/out Ljava/io/PrintStream;
    3: iload 0
    4: ifeq 7
    5: ldc 0
    6: goto 8
    7: ldc 1
    8: invokevirtual java/io/PrintStream/print(Z)V
    9: getstatic java/lang/System/out Ljava/io/PrintStream;
    10: ldc "\n"
    11: invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    12: return
.end method
