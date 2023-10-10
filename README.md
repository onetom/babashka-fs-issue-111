# babashka-fs-issue-111

Demo/repro for https://github.com/babashka/fs/issues/111

See `src/repl/s3.clj` for using the S3 `Path`s.
It shows how `ginoco.fs/path` fails to extend an existing S3 path.

The examples use our `ginoco.fs` namespace, an extremely minimal NIO.2 wrapper.
It's only included to show how little wrapping was satisfactory for our use-case
of reading some template Excel files from S3, modifying some sheets in them,
then uploading the resulting workbook back to S3 into some other bucket for
later download.
