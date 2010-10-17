# GreenScript

# ~~~~~~~~~~~~~~~~~~~~~~ [greenscript:cp] Copy tags or templates
try:
    play_command
except NameError:
    play_command = None
if play_command is None:
    # ~~~~~~~~~~~~~~~~~~~~~ running on v1.1
    import getopt
    from play.utils import *
    
    COMMANDS = ['greenscript:cp', 'greenscript:copy']
    HELP = {
        'greenscript:copy': 'copy tags or templates to your app'
    }
    def execute(**kargs):
        app = kargs.get("app")
        remaining_args = kargs.get("args")
        play_env = kargs.get("env")
    
        try:
            optlist, args = getopt.getopt(remaining_args, 't:a:', ['template=', 'tag='])
            for o, a in optlist:
                if o in ('-a', '--tag'):
                    if a == '.':
                        toDir = 'app/views/tags' 
                    else:
                        toDir = 'app/views/tags/%s' % a
                    fromDir = 'app/views/tags/greenscript'
                    for f in ('js.html', 'css.html'):
                        app.override('%s/%s' % (fromDir, f), '%s/%s' % (toDir, f))
                    print "~ "
                    return
                    
                if o in ('-t', '--template'):
                    app.override('app/views/greenscript/Configurator/configure.html', 'app/views/%s/configure.html' % a)
                    print "~ "
                    return
    
        except getopt.GetoptError, err:
            print "~ %s" % str(err)
            print "~ "
            sys.exit(-1)
    
        print "~ Copy greenscript tag or configurator template to your app" 
        print "~ "
    

else:
    # ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ running on v1.0
    if play_command == 'greenscript:cp' or play_command == 'greenscript:copy':
        try:
            optlist, args = getopt.getopt(remaining_args, 'a:t:', ['tag=','template='])
            for o, a in optlist:
                if o in ('-a', '--tag'):
                    if a == '.':
                        toDir = 'app/views/tags' 
                    else:
                        toDir = 'app/views/tags/%s' % a
                    fromDir = 'app/views/tags/greenscript'
                    for f in ('js.html', 'css.html'):
                        override('%s/%s' % (fromDir, f), '%s/%s' % (toDir, f))
                    print "~ "
                    sys.exit(0)
                
                if o in ('-t', '--template'):
                    override('app/views/greenscript/Configurator/configure.html', 'app/views/%s/configure.html' % a)
                    print "~ "
                    sys.exit(0)
                    
        except getopt.GetoptError, err:
            print "~ %s" % str(err)
            print "~ "
            sys.exit(-1)
        
        print "~ Use -a | --tag to copy the greenscript tag" 
        print "~ Use -t | --template to copy the greenscript configurator template" 
        print "~ "
            
        sys.exit(0)