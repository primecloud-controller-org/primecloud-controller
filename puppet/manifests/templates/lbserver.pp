class lbserver {

}

class lbserver::ultramonkey inherits lbserver {
#    include ultramonkey
}

#class lbserver::ultramonkey::default inherits lbserver::ultramonkey{
#
#}

class lbserver::ultramonkey::resource {

    ultramonkey::config { "Default Config" : }

}

class lbserver::ultramonkey::stop {

    ultramonkey::stop { "Stop": }

}
