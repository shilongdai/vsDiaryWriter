#!/bin/bash
rm -rf build;
mkdir build;
cd build;
echo "copying src files";
cp -rv ../journal journalWindows32;
cp -rv ../journal journalLinux32;
cp -rv ../journal journalLinux64;
cp -rv ../journal journalMac64;
echo "configuring build dependencies";
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.win32.win32.x86/g' journalWindows32/pom.xml;
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.gtk.linux.x86/g' journalLinux32/pom.xml;
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.cocoa.macosx.x86_64/g' journalMac64/pom.xml;

xulRunner[0]="xulrunner-24.0.en-US.linux-i686.tar.bz2";
xulRunner[1]="xulrunner-24.0.en-US.linux-x86_64.tar.bz2";
xulRunner[2]="xulrunner-24.0.en-US.mac.tar.bz2";
xulRunner[3]="xulrunner-24.0.en-US.win32.zip";
mozilla="https://ftp.mozilla.org/pub/xulrunner/releases/24.0/runtimes/"

echo "starting build";
for i in $(ls); do
	echo "building for $i";
	
	#run maven
	cd $i;
	mvn install;

	#copy editor
	cd target;
	mv ../editor .;

	#get XULRunner
	if [ $i = "journalWindows32" ]; then
		wget $mozilla${xulRunner[3]};
		unzip ${xulRunner[3]};
	fi

	if [ $i = "journalLinux32" ]; then
		wget $mozilla${xulRunner[0]};
		tar -jxvf ${xulRunner[0]};
	fi

	if [ $i = "journalLinux64" ]; then
		wget $mozilla${xulRunner[1]};
		tar -jxvf ${xulRunner[1]};
	fi

	if [ $i = "journalMac64" ]; then
		wget $mozilla${xulRunner[2]};
		tar -jxvf${xulRunner[2]};
	fi

	#sign jar file
	jarFileName=$(ls | grep full);
        /usr/java/default/bin/jarsigner -tsa http://timestamp.digicert.com -keystore $2 -storepass Mj2000629@DvpNt $jarFileName viperfish

	#zip it up
	zip -r $i.zip $jarFileName editor xulrunner;
	echo "copying product to $1";
	mv $i.zip $1;
	cd ../..;
done;

echo "cleaning up";
cd ..;
rm -rf build;
