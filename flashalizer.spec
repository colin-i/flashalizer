Name:           flashalizer
Version:        1.6
Release:        0
Summary:        GUI to make .swf files

License:        GPLv3+
URL:            https://github.com/colin-i/%{name}
Source0:        %{name}-%{version}.tar.gz

BuildRequires:  ant
BuildRequires:  java-devel >= 1:1.8
BuildRequires:  javapackages-tools
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

%build
ant build

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
%license readme.md
%doc readme.md
%{_bindir}/%{name}
%{_javadir}/%{name}.jar
%{_datadir}/pixmaps/%{name}.jpg
%{_datadir}/applications/%{name}.desktop

%changelog
* Wed Oct 07 2026 costin <costin.botescu@gmail.com> 1.6-0
- 

