Name:           flashalizer
Version:        1.6
Release:        1%{?dist}
Summary:        GUI to make .swf files

License:        GPLv3+
URL:            https://github.com/colin-i/%{name}
Source0:        %{name}-%{version}.tar.gz

BuildArch:      noarch

BuildRequires:  ant
BuildRequires:  java-devel >= 1:1.8
BuildRequires:  jna
BuildRequires:  jna-contrib
BuildRequires:  javassist

Requires:       java >= 1:1.8
Requires:       jna
Requires:       jna-contrib
Requires:       javassist
Requires:       actionswf

%description
GUI to make .swf files with actionswf.

%prep
%autosetup
# Use Fedora-specific manifest
cp MANIFEST.f.MF MANIFEST.MF

%build
mkdir libs
ln -s /usr/share/java/jna.jar libs/jna.jar
#this jna-platform needs to be found somehow
ln -s /usr/share/java/jna/jna-platform.jar libs/jna-platform.jar
ln -s /usr/share/java/javassist.jar libs/javassist.jar
ant -Djava.lib=$PWD/libs build

%install
rm -rf %{buildroot}
mkdir -p %{buildroot}%{_bindir}
mkdir -p %{buildroot}%{_javadir}

# Install jar
install -m 644 dist/flashalizer.jar %{buildroot}%{_javadir}/%{name}.jar

# Install wrapper script with JVM options (matching Debian package)
cat > %{buildroot}%{_bindir}/%{name} << 'EOF'
#!/bin/sh
exec java --add-opens=java.base/java.lang=ALL-UNNAMED --enable-native-access=ALL-UNNAMED -jar /usr/share/java/%{name}.jar "$@"
EOF
chmod 755 %{buildroot}%{_bindir}/%{name}

# Install icon
mkdir -p %{buildroot}%{_datadir}/pixmaps
install -m 644 img/icon.jpg %{buildroot}%{_datadir}/pixmaps/%{name}.jpg

# Install desktop file
mkdir -p %{buildroot}%{_datadir}/applications
cat > %{buildroot}%{_datadir}/applications/%{name}.desktop << 'EOF'
[Desktop Entry]
Version=1.0
Type=Application
Name=Flashalizer
Comment=GUI to make .swf files
Exec=/usr/bin/%{name}
Icon=/usr/share/pixmaps/%{name}.jpg
Terminal=false
Categories=Utility;Development;
EOF

%files
%license LICENSE
%doc INFO
%{_bindir}/%{name}
%{_javadir}/%{name}.jar
%{_datadir}/pixmaps/%{name}.jpg
%{_datadir}/applications/%{name}.desktop

%changelog
* Fri Oct 09 2026 costin <costin.botescu@gmail.com> 1.6-1
- Automatic commit of package [flashalizer] release [1.6-1].
  (costin.botescu@gmail.com)
- Automatic commit of package [flashalizer] release [1.6-1].
  (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- pkg yml (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- rel.yml (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- - 1 req (costin.botescu@gmail.com)

* Wed Oct 07 2026 costin <costin.botescu@gmail.com> 1.6-0
- 

