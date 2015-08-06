#!/bin/bash

# Exit on errors
set -e


$1  -j 8 USE_BREAKPAD=0


if [ $? = 0 ]; then
	rm -rf ovpnlibs/

	cd libs
	mkdir -p ../ovpnlibs/assets
	for i in *
	do
		cp -v $i/nopievpn ../ovpnlibs/assets/nopievpn.$i
		cp -v $i/pievpn ../ovpnlibs/assets/pievpn.$i
	done
	# Removed compiled openssl libs, will use platform so libs 
	# Reduces size of apk
    #
	rm -v */libcrypto.so */libssl.so

  	for arch in *
  	do
  	    builddir=../ovpnlibs/jniLibs/$arch
  	    mkdir -p $builddir
  		cp -v $arch/*.so  $builddir
  	done
else
    exit $?
fi
