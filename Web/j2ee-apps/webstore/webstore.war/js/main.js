require.config({
    paths: {
        jquery: 'js/lib/jquery-2.2.1',
        'jquery-form': 'js/lib/jquery.form',
        'form-submit':  'js/form.utils'
    },
    shim: {
    	'jquery-form': {
            exports: '$',
            deps: ['jquery']
        },
        'jquery-validate': {
        	expors: '$',
        	deps: ['jquery']
        }
    }
});

