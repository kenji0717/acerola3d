#!/bin/bash

# function to find the real file by pursuing symbolic links
# the argument must be an absolute path or a relative path from
# current directory. The return value is always an absolute path.
# This function is a substitute of command `readlink -f` on linux.
# On Mac OS X, "-f" option is not supported.
# http://stackoverflow.com/questions/1055671/how-can-i-get-the-behavior-of-gnus-readlink-f-on-a-mac
pursueSymbolicLink() {
  #_dirBackup=$(pwd)
  _TARGET_FILE=$1
  cd ${_TARGET_FILE%/*}
  _TARGET_FILE=${_TARGET_FILE##*/}
  while [ -L "$_TARGET_FILE" ]
  do
    _TARGET_FILE=$(readlink $_TARGET_FILE)
    cd ${_TARGET_FILE%/*}
    _TARGET_FILE=${_TARGET_FILE##*/}
  done
  _TARGET_FILE=$(pwd -P)/$_TARGET_FILE
  #cd $_dirBackup
  echo $_TARGET_FILE
}

SCRIPT_FILE=$(pursueSymbolicLink $0)
INSTALL_DIR=$(dirname $SCRIPT_FILE)

CLASSPATH=".:$INSTALL_DIR/lib/*:$INSTALL_DIR/a3files:$A3FILES:$CLASSPATH"
export CLASSPATH
PATH="$INSTALL_DIR/Java/jdk/bin:$INSTALL_DIR/Java/jre/bin:$PATH"
export PATH
DYLD_LIBRARY_PATH="$INSTALL_DIR/natives/macosx-universal"
export DYLD_LIBRARY_PATH

if [ $# -eq 0 ] ; then
    java jp.sourceforge.acerola3d.a3viewer.A3Viewer
else
    java jp.sourceforge.acerola3d.a3viewer.A3Viewer -open $1
fi
