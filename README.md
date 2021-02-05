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

## How it works

SigClone is the result of my master thesis, and the details are described in there.
It is not currently published, but if you are interested, I can email you a copy, if you reach out to me.

A short overview:
SigClone extracts the method signatures, consisting of the return type, the method identifier, and the parameters, each consisting of a type and a name.
The implicit `this`-parameter is also considered, if applicable.
The method signatures are then vectorized and finally compared to each other.
If two signatures are similar enough, according to some similarity measure and a threshold, they are considered clones.
SigClone supports different similarity measures, one of them including an AI trained for natural language processing.
The different approaches are described and evaluated in my master thesis.

## FAQ

**What languages does SigClone support?** - Only Java, currently. Feel free to add a parser for your favorite language and file a PR.

**Does my code need to compile?** - No, a compilation is not required. You can easily supply sub-sets of your codebase to SigClone, or even files from different projects.

**What similarity measures are supported?** - In short: Euclidean distance, relative word distance (not an official term), and cosine distance.
More detail is available in the code (look [here](https://github.com/qw3ry/sigclone/tree/master/src/main/kotlin/main/runners)), or in my thesis.
Feel free to code another similarity measure and file a PR.
