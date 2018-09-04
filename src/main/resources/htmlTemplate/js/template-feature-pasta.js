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
var substringMatcher = function(strs) {
    return function findMatches(q, cb) {
        var matches = [];
        var substrRegex = new RegExp(q, 'i');

        $.each(strs, function(featureId, values) {
            $.each(values, function(i, txt) {
                if (substrRegex.test(txt)) {
                    matches.push({
                        "feature":featureId,
                        "txt":txt
                    });
                }
            });
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
    source: substringMatcher(indice),
    templates: {
        suggestion: Handlebars.compile("<div><p><strong>{{feature}}</strong></p>{{txt}}</div>")
    }
}).bind('typeahead:select', function(ev, suggestion) {
    window.location = "#/feature/" + suggestion.feature + '/' + encodeURIComponent(suggestion.txt);
});