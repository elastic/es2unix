Name:           es2unix
Version:        1.6.1
Release:        0%{?dist}
Summary:        Command-line ES

Group:          Applications/System
License:        Apache License 2.0
URL:            https://github.com/elasticsearch/es2unix
Source0:        %{name}-%{version}.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildArch:      noarch
BuildRequires:  leiningen
Requires:       java >= 1.7.0

%description

Elasticsearch API consumable by the command line.

JSON isn't always the most convenient output, particularly on a
terminal. The tabular format has stuck around for good reason. It's
compact. It's line-oriented. es2unix strives to keep spaces
significant so all output works with existing *NIX tools. grep, sort,
& awk are first-class citizens here.

%prep
%setup -q

%build
make package

%install
rm -rf %{buildroot}
install -m 755 -D target/es %{buildroot}/%{_bindir}/es

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)
%doc README.md LICENSE
%{_bindir}/es

%changelog
* Fri Sep 06 2014 Trevor Vaughan <tvaughan@onyxpoint.com> - 1.6.1-0
- First cut an a es2unix RPM
