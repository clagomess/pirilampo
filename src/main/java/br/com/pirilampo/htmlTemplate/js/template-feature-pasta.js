$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});

$("#btn-master").click(function(){
    if($('[ng-view]').attr('class').indexOf('col-lg-12') != -1){
        $('[ng-view]').attr('class', 'col-lg-6');
        $('#feature-master').show();
    }else{
        $('[ng-view]').attr('class', 'col-lg-12');
        $('#feature-master').hide();
    }
});

$("#btn-diff").click(function(){
    $('#feature-diff-row').toggle();
});

// TYPE HEAD
var source = [];

$('[type="text/ng-template"]').each(function(){
    var feature = $(this).attr('id');
    var matches = $(this).html().match(/(>|\n)([^<>].+?)(<|\n)/gm);

    if(feature.indexOf('master_') != -1){
        return;
    }

    for(var i in matches){
        if(matches.hasOwnProperty(i)) {
            var match = matches[i].replace(/^>/, '');
            match = match.replace(/<$/g, '');
            match = match.replace(/<(|\/)(\w*)>/g, '');
            match = match.trim();

            if(match.length >= 3) {
                var item = {
                    feature: feature.replace('.html', ''),
                    txt: $('#buscaBuffer').html(match).text()
                };

                // verifica repetido
                if(source.indexOf(item) == -1) {
                    source.push(item);
                }
            }
        }
    }
});

var substringMatcher = function(strs) {
    return function findMatches(q, cb) {
        var matches = [];
        var substrRegex = new RegExp(q, 'i');

        $.each(strs, function(i, str) {
            if (substrRegex.test(str.txt)) {
                matches.push(str);
            }
        });

        cb(matches);
    };
};

$('#busca').typeahead({
    hint: false
}, {
    limit: 20,
    displayKey: 'txt',
    name: 'txt',
    display: 'txt',
    source: substringMatcher(source),
    templates: {
        suggestion: Handlebars.compile("<div><p><strong>{{feature}}</strong></p>{{txt}}</div>")
    }
}).bind('typeahead:select', function(ev, suggestion) {
    window.location = "#/feature/" + suggestion.feature + '/' + encodeURIComponent(suggestion.txt);
});