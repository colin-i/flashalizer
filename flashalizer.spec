Name:           flashalizer
Version:        1.5
Release:        0
Summary:        GUI to make .swf files

License:        GPLv3
URL:            https://github.com/colin-i/%{name}
Source0:        %{name}-%{version}.tar.gz

BuildRequires:  #
Requires:       #

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
