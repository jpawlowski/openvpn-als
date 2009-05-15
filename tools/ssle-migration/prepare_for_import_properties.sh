#!/bin/bash

BASEDIR=/home/sasepp/adito-import

ORIG=$BASEDIR/english
TRANSL=$BASEDIR/french

# Leave empty if required
# SRCLANG=en_US
# Leave empty if the language code is already appended
# e.g. ApplicationResources_fr.properties
# DSTLANG=fr

cd $ORIG
find -name "*.properties"|sed s/"\/"/-/g|sed s/-//1|sed s/\.//1


read yn

cd $TRANSL
find -name "*.properties"|sed s/"\/"/-/g|sed s/-//1|sed s/\.//1


