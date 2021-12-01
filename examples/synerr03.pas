program comment(input, output);

    begin
        { Comment 1 (* comment 2 *) }
        (* Comment 1 { comment 2 } *)
        { comment 1 // Comment 2 }
        (* comment 1 // Comment 2 *)
        // comment 1 (* comment 2 *)
        // comment 1 { comment 2 }
    end (* missing "." *)

