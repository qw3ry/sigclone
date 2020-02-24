# SigClone - the signature based clone detector

SigClone is a clone detector comparing only method signatures.
This allows the detection of truely semantic clones.

## Building

SigClone is built using gradle.
You need to build two targets: `jar` and `librariesJar`.
The former only builds the source code, the latter creates a jar containing its dependencies.

## Usage

To execute SigClone, run `java -jar sigclone.jar --help`.
The libraries jar created during the build should be included in the classpath automatically.
When called with `--help`, SigClone displays an appropriate help message.
This should aid you with the further usage.


