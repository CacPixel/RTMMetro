#*-------------------------------------------------------------------------
#   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
#   RXTX is a native interface to serial ports in java.
#   Copyright 1997-2008 by Trent Jarvi tjarvi@qbang.org and others who
#   actually wrote it.  See individual source files for more information.
#
#   A copy of the LGPL v 2.1 may be found at
#   http://www.gnu.org/licenses/lgpl.txt on March 4th 2007.  A copy is
#   here for your convenience.
#
#   This library is free software; you can redistribute it and/or
#   modify it under the terms of the GNU Lesser General Public
#   License as published by the Free Software Foundation; either
#   version 2.1 of the License, or (at your option) any later version.
#
#   This library is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
#   Lesser General Public License for more details.
#
#   An executable that contains no derivative of any portion of RXTX, but
#   is designed to work with RXTX by being dynamically linked with it,
#   is considered a "work that uses the Library" subject to the terms and
#   conditions of the GNU Lesser General Public License.
#
#   The following has been added to the RXTX License to remove
#   any confusion about linking to RXTX.   We want to allow in part what
#   section 5, paragraph 2 of the LGPL does not permit in the special
#   case of linking over a controlled interface.  The intent is to add a
#   Java Specification Request or standards body defined interface in the 
#   future as another exception but one is not currently available.
#
#   http://www.fsf.org/licenses/gpl-faq.html#LinkingOverControlledInterface
#
#   As a special exception, the copyright holders of RXTX give you
#   permission to link RXTX with independent modules that communicate with
#   RXTX solely through the Sun Microsytems CommAPI interface version 2,
#   regardless of the license terms of these independent modules, and to copy
#   and distribute the resulting combined work under terms of your choice,
#   provided that every copy of the combined work is accompanied by a complete
#   copy of the source code of RXTX (the version of RXTX used to produce the
#   combined work), being distributed under the terms of the GNU Lesser General
#   Public License plus this exception.  An independent module is a
#   module which is not derived from or based on RXTX.
#
#   Note that people who make modified versions of RXTX are not obligated
#   to grant this special exception for their modified versions; it is
#   their choice whether to do so.  The GNU Lesser General Public License
#   gives permission to release a modified version without this exception; this
#   exception also makes it possible to release a modified version which
#   carries forward this exception.
#
#   You should have received a copy of the GNU Lesser General Public
#   License along with this library; if not, write to the Free
#   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#   All trademarks belong to their respective owners.
#-------------------------------------------------------------------------*/
#  This Makefile works on windows 98/NT with mingw32 in a DOS shell.
#  Do not run in MSYS! Last tested on WinXP with MinGW-5.1.4
#  (gcc-3.4.5, binutils-2.17.50, mingw32-make-3.81, w32api-3.12). 
#
#  SET PATH=c:\mingw\bin;c:\jdk142\bin;C:\WINDOWS\SYSTEM32
#  mkdir build-mingw
#  copy ..\Makefile.mingw32 build-mingw\Makefile
#  cd build-mingw
#  REM Edit Makefile settings to change user variables as needed
#  mingw32-make SHELL=cmd
#  mingw32-make SHELL=cmd install

######################
#  user defined variables
######################

# path to the source code (directory with SerialImp.c) Unix style path
SRC=../src

# path to the jdk directory that has include, bin, lib, ... Unix style path
JDKHOME=D:\Program Files\Java\jdk1.8.0_202

#path to mingw32
MINGHOME=D:\Chocolatey\lib\mingw\tools\install\mingw64

# path to install RXTXcomm.jar DOS style path
COMMINSTALL="$(JDKHOME)\jre\lib\ext"

# path to install the shared libraries DOS style path
LIBINSTALL="$(JDKHOME)\jre\bin"

# path to the mingw32 libraries (directory with libmingw32.a) DOS style path
LIBDIR="$(MINGHOME)\lib"

######################
#  End of user defined variables
######################


######################
#  Tools
######################

AS=as
# CC=gcc
CC=gcc
LD=ld
DLLTOOL=dlltool
# this looks like a nice tool but I was not able to get symbols in the dll.
DLLWRAP=dllwrap

CLASSPATH=-classpath ".;C:\jdk13\lib\RXTXcomm.jar;c:\BlackBox.jar;$(JDKHOME)\lib\classes.zip;$(JDKHOME)\jre\lib\rt.jar"
JAVAH="$(JDKHOME)\bin\javah" $(CLASSPATH)
JAR="$(JDKHOME)\bin\jar"
JAVAC="$(JDKHOME)\bin\javac" -source 1.2 -target 1.2 -g $(CLASSPATH)
#JAVAC=$(JDKHOME)\bin\javac -g $(CLASSPATH)


######################
#  Tool Flags
######################

CFLAGS_CYGWIN= -O2 $(INCLUDE) -DWIN32 -D __int64="long long" -mno-fp-ret-in-387  -Wall 
# CFLAGS= -O2 $(INCLUDE) -mno-cygwin -DWIN32 -D_JNI_IMPLEMENTATION_ -D __int64="long long" -mno-fp-ret-in-387  -Wall
CFLAGS= -O2 $(INCLUDE) -DWIN32 -D_JNI_IMPLEMENTATION_ -D __int64="long long" -mno-fp-ret-in-387  -Wall
#CFLAGS= -O2 $(INCLUDE) -mno-cygwin -DWIN32 -D_JNI_IMPLEMENTATION_ -D __int64="long long" -mno-fp-ret-in-387 -Wall -D TRENT_IS_HERE_DEBUGGING_ENUMERATION -DTRENT_IS_HERE_DEBUGGING_THREADS
INCLUDE= -I . -I"$(JDKINCLUDE)" -I"$(JDKINCLUDE)/win32" -I"$(SRC)" -I include -I"$(MINGINCLUDE)"
LIBS=-L . -L $(LIBDIR) -LC:/tmp/rxtx5/build -luser32 -lgdi32 -lcomdlg32 -lkernel32 -ladvapi32 -lmoldname -lcrtdll
# -lmex

# path to the java native interface headers (directory with jni.h)
JDKINCLUDE=$(JDKHOME)/include
MINGINCLUDE=$(MINGHOME)/include
JAVAFILES = $(wildcard $(SRC)/gnu/io/*.java)
CFILES=$(wildcard $(SRC)/*.c) $(wildcard $(SRC)/*.cc)
CLASSES=$(wildcard ./**/*.class)
TARGETLIBS= rxtxSerial.dll rxtxParallel.dll
#TARGETLIBS= rxtxSerial.dll rxtxParallel.dll rxtxZystem.dll
DLLOBJECTS= fixup.o SerialImp.o termios.o init.o fuserImp.o
PARALLELOBJECTS= ParallelImp.o termios.o init.o

all: $(TARGETLIBS) # rebuild  rebuild will force everything to be built.

rebuild: 
	-rmdir /s /q gnu
	-del /q RXTXcomm.jar rxtxSerial.* rxtxParallel.* *.o 

# FIXME make 3.79.1 behaves really strage if we use %.o rules.

init.o:	$(SRC)/init.c
	$(CC) $(CFLAGS) -c $(SRC)/init.c -o init.o

fixup.o: $(SRC)/fixup.c
	$(CC) $(CFLAGS) -c $(SRC)/$*.c -o fixup.o

#ZystemImp.o:  config.h
#	$(CC) $(CFLAGS_CYGWIN) -c $(SRC)/ZystemImp.c -o ZystemImp.o

SerialImp.o: $(SRC)/SerialImp.c $(SRC)/SerialImp.h config.h gnu_io_RXTXPort.h
	$(CC) $(CFLAGS) -c $(SRC)/SerialImp.c -o SerialImp.o

fuserImp.o: $(SRC)/fuserImp.c config.h gnu_io_CommPortIdentifier.h
	$(CC) $(CFLAGS) -c $(SRC)/fuserImp.c -o fuserImp.o

termios.o: $(SRC)/termios.c
	$(CC) $(CFLAGS) -c $(SRC)/$*.c -o termios.o

ParallelImp.o: $(SRC)/ParallelImp.c $(SRC)/ParallelImp.h $(SRC)/win32termios.h config.h gnu_io_LPRPort.h
	$(CC) $(CFLAGS) -c $(SRC)/$*.c -o ParallelImp.o

#  This file is a pain in the rear to generate.  If your looking at this you
#  need to install cygwin.

rxtxZystem.dll:	RXTXcomm.jar $(DLLOBJECTS) $(SRC)/ZystemImp.c #ZystemImp.o
	gcc  -c -DBUILDING_DLL=1 -D_DLL=1  -I. -Ic:/jdk13/include -Ic:/jdk13/include/win32  -Wall -O2 -D __int64="long long" -o ZystemImp.o $(SRC)/ZystemImp.c
	dllwrap --verbose --no-default-excludes --export-all-symbols --output-exp Zystem.exp --output-def Zystem.def --add-stdcall-alias --driver-name gcc  -o rxtxZystem.dll ZystemImp.o -Wl,-e,__cygwin_noncygwin_dll_entry@12 -s

rxtxSerial.dll:	RXTXcomm.jar $(DLLOBJECTS)
	gcc -shared -o $@ $(DLLOBJECTS) -lmingw32 -Wl,--kill-at

rxtxParallel.dll: $(PARALLELOBJECTS)
	gcc -shared -o $@ $(PARALLELOBJECTS) -lmingw32 -Wl,--kill-at

gnu_io_RXTXPort.h gnu_io_CommPortIdentifier.h gnu_io_LPRPort.h gnu_io_RXTXVersion.h: RXTXcomm.jar
	$(JAVAH) -jni gnu.io.RXTXPort gnu.io.CommPortIdentifier gnu.io.LPRPort gnu.io.RXTXVersion

RXTXcomm.jar: $(JAVAFILES)
	$(JAVAC) -d . $(JAVAFILES)
	$(JAR) -cf RXTXcomm.jar gnu

config.h: Makefile
	echo #define HAVE_FCNTL_H 1 > config.h
	echo #define HAVE_SIGNAL_H 1 >> config.h
	echo #define HAVE_SYS_FCNTL_H 1 >> config.h
	echo #define HAVE_SYS_FILE_H 1 >> config.h
	echo #undef HAVE_SYS_SIGNAL_H >> config.h
	echo #undef HAVE_TERMIOS_H >> config.h

install: all
	xcopy /Y RXTXcomm.jar $(COMMINSTALL)
	@REM xcopy RXTXcomm.jar "e:\matlab~1\java\jarext\commapi\win32"
	xcopy /Y rxtx*.dll $(LIBINSTALL)
	@REM xcopy $(TARGETLIBS) "e:\matlab~1\bin\win32"

uninstall:
	del $(COMMINSTALL)\\\RXTXcomm.jar
	del $(LIBINSTALL)\\\rxtx*.dll 

clean:
	-rmdir /s /q gnu
	-del /Q *.o RXTXcomm.jar rxtxSerial.dll rxtxParallel.dll config.h gnu_io_*.h
