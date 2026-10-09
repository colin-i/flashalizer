# Flashalizer

## Install
On Ubuntu(resolute) from PPA.
```sh
sudo add-apt-repository ppa:colin-i/ppa
```
Or the *manual installation step* from this [link](https://gist.github.com/colin-i/e324e85e0438ed71219673fbcc661da6#manual-installation-step).\
Install:
```sh
sudo apt-get install flashalizer
```
\
On Fedora 43/44:
```sh
sudo dnf copr enable colin/project
sudo dnf install flashalizer
```
\
On Arch Linux, <i>.zst</i> file from [releases](https://github.com/colin-i/flashalizer/releases). Or:
```sh
yay -Sy flashalizer
```
\
On linux distributions(x86_64), <i>.AppImage</i> file from [releases](https://github.com/colin-i/flashalizer/releases).\
\
\
On multiple platforms with java installed, <i>.jar</i> file from [releases](https://github.com/colin-i/flashalizer/releases), and having the requirements.

## From source
### Requirements
- clone the project with: `git clone https://github.com/colin-i/flashalizer.git`
- ActionSwf is [here](https://github.com/colin-i/actionswf)
- download Java Native Access from https://github.com/twall/jna (jna and jna-platform)
- download Javassist from https://github.com/jboss-javassist/javassist/releases
- only for some functions: dbl2png and png2dbl are with ming at http://www.libming.org/ ; on windows are found with cygwin
### Compile and run
```sh
ant compile
ant run
```

## Info
- if starting with an argument, the argument will be the project folder, same as File > Open
- at Shape>Edit , to add control points, select two direct linked points and click at one position to place the control point
- png image: https://drive.google.com/open?id=1C1cCOCPH8S_PhxsPHewIyNgUv4kKg-lz

## Donations
The *donations* section is [here](https://gist.github.com/colin-i/e324e85e0438ed71219673fbcc661da6#donations).
