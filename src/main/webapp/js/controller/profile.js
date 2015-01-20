$(function() {
	
	/**
	 * Inicializa a combos Data de nascimento
	 */
	App.loadDateCombos($("#birthday"), $("#birthday-month"), $("#birthday-year"));
	
	/**
	 * Habilita o autocomplete no campo Cidade de residência
	 */
	$("#city").autocomplete({
		source : function(request, response) {
			LocationProxy.searchCity(request.term).done(function(data) {
				response(convertToLabelValueStructure(data));
			});
		},
		minLength : 3,
		select : function(event, ui) {
			$("#city").val(ui.item.label.split("/")[0]);
			$("#city\\.id").val(ui.item.value);
			return false;
		},
		change : function(event, ui) {
			$("#city\\.id").val(ui.item ? ui.item.value : null);
			return false;
		},
		focus : function(event, ui) {
			$("#city\\.id").val(ui.item.value);
			$("#city").val(ui.item.label.split("/")[0]);
			return false;
		}
	});
	
	/**
	 * Cadastro dos dados pessoais
	 */
	$('#bt-save').on('click', function(e) {
		var birthday = "";

		if (!isNaN($("#birthday-year").val()) && !isNaN($("#birthday-month").val()) && !isNaN($("#birthday").val())) {
			birthday = $("#birthday-year").val() + "-" + $("#birthday-month").val() + "-" + $("#birthday").val();
		}

		var data = {
			'name' : $("#name").val(),
			'birthday' : birthday,
			'rg' : $("#rg").val(),
			'cpf' : $("#cpf").val(),
			'city' : {
				"id" : $("#city\\.id").val()
			},
			'gender' : $("#gender").val(),
			'mobile' : $("#mobile").val()
		};
		ProfileProxy.update(data).done(updateOk).fail(updateFail);
	});
	
	/**
	 * Carrega os dados pessoais
	 */
	ProfileProxy.load().done(loadOk);
});

/* ---------------- Funções de Callback ---------------- */

/**
 * Função que carrega os dados pessoais do usuário.
 */
function loadOk(data) {
	console.log('loadOk');
	$("#name").val(data.name);
	$("#rg").val(data.rg);
	$("#cpf").val(data.cpf);

	if (data.birthday) {
		$("#birthday-year").val(parseInt(data.birthday.split("-")[0]));
		$("#birthday-month").val(parseInt(data.birthday.split("-")[1]));
		$("#birthday").val(parseInt(data.birthday.split("-")[2]));
	}

	$("#gender").val(data.gender);
	$("#city\\.id").val(data.city.id);
	$("#city").val(data.city.name);
	$("#mobile").val(data.mobile);
}

/**
 * 
 */
function updateOk(data) {
	console.log('updateOk');
	$("[id$='-message'").hide();
	$("#global-message").text("Dados atualizados com sucesso.").addClass("alert-success").show();
}

/**
 * Tratamento de erro dos dados básicos.
 */
function updateFail(request) {
	console.log('updateFail');
	$("#global-message").text("").removeClass("alert-success").hide();
}


/* ---------------- Funções Utilitárias ---------------- */

/**
 * Função utilitária que converte o objeto retornado no suggest para o formato
 * do jqueryUi.
 */
function convertToLabelValueStructure(data) {
	var newData = [];
	$.each(data, function() {
		newData.push({
			"label" : this.name + "/" + this.state,
			"value" : this.id
		});
	});
	return newData;
}