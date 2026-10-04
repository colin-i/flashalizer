Name:           flashalizer
Version:        1.6
Release:        0
Summary:        GUI to make .swf files

License:        GPLv3
URL:            https://github.com/colin-i/%{name}
Source0:        %{name}-%{version}.tar.gz

#BuildRequires:  #
#Requires:       #

%description
GUI to make .swf files with actionswf.

%prep
%autosetup


%build
%configure
%make_build


%install
%make_install


%files
%license add-license-file-here
%doc add-docs-here



%changelog
* Sun Oct 04 2026 costin <costin.botescu@gmail.com> 1.6-0
- Automatic commit of package [flashalizer] release [1.6-0].
  (costin.botescu@gmail.com)
- deb fix (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "sync" (mail@flashixy.com)
- deb (costin.botescu@gmail.com)
- updating readme (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- upapp (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- appimage actionswf (costin.botescu@gmail.com)
- steps at appimage (costin.botescu@gmail.com)
- start args (costin.botescu@gmail.com)
- appimage steps (costin.botescu@gmail.com)
- test.yml (costin.botescu@gmail.com)
- unused imports (costin.botescu@gmail.com)
- data at appimage (costin.botescu@gmail.com)
- user.dir (costin.botescu@gmail.com)
- fix to compile on latest jdk (costin.botescu@gmail.com)
- clarity (costin.botescu@gmail.com)
- swf_img pref . setlocale pref . (costin.botescu@gmail.com)
- ported to linux (costin.botescu@gmail.com)
- "up" (costin.botescu@gmail.com)
- "sync" (costin.b.84@gmail.com)
- multiple wine contexts (costin.botescu@gmail.com)
- new actionswf (costin.botescu@gmail.com)
- asflags (costin.botescu@gmail.com)
- "sync" (costin.b.84@gmail.com)

