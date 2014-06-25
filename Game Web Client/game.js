window.requestAnimationFrame = (window.msRequestAnimationFrame || window.mozRequestAnimationFrame || window.webkitRequestAnimationFrame || window.oRequestAnimationFrame || window.requestAnimationFrame);

function Client() {
	this.guiScreen = new GuiMainMenu();
	console.log('Game started');
	if($)
		console.log($);
	this.gameWindow = $('#game-window');
	this.guiScreen.render(this.gameWindow);
	this.lastGuiScreen = this.guiScreen;
};

Client.prototype.render = function() {
	console.log('render');
	window.requestAnimationFrame(this.render.bind(this));
};

Client.prototype.setGuiScreen = function(guiScreen) {
	this.lastGuiScreen = this.guiScreen;
	this.guiScreen = guiScreen;
	this.gameWindow.attr('class','');
	this.gameWindow.html('');
	console.log(guiScreen.constructor.name);
	guiScreen.render(this.gameWindow);
};

function GuiMainMenu(){}

GuiMainMenu.prototype.render = function(gameWindow){
	gameWindow.addClass('background');
	gameWindow.addClass('menu');
	gameWindow.addClass('guimainmenu');
	var title = $('<h1>Game Title</h1>').addClass('title');
	gameWindow.append(title);
	var buttons = $('<div></div>').addClass('buttons');
	buttons.append($('<button>New Game</button>').click(function(){client.setGuiScreen(new GuiSelectServer());}));
	buttons.append($('<button>Options</button>').click(function(){client.setGuiScreen(new GuiOptions());}));
	buttons.append($('<button>Credits</button>'));
	buttons.append($('<button>Test</button>').addClass('small'));
	gameWindow.append(buttons);
};

function GuiOptions(){}

GuiOptions.prototype.render = function(gameWindow){
	gameWindow.addClass('background');
	gameWindow.addClass('menu');
	gameWindow.addClass('guioptions');
	var title = $('<h1>Options</h1>').addClass('title');
	gameWindow.append(title);
	var buttons = $('<div></div>').addClass('buttons');
	buttons.append($('<button>Back</button>').click(function(){client.setGuiScreen(client.lastGuiScreen);}));
	gameWindow.append(buttons);
};

function GuiSelectServer(){}

GuiSelectServer.prototype.render = function(gameWindow){
	gameWindow.addClass('background');
	gameWindow.addClass('menu');
	gameWindow.addClass('guioptions');
	var title = $('<h1>Select Server</h1>').addClass('title');
	gameWindow.append(title);
	var buttons = $('<div></div>').addClass('buttons');
	buttons.append($('<button>Back</button>').click(function(){client.setGuiScreen(client.lastGuiScreen);}));
	gameWindow.append(buttons);
};

var client = new Client();
//client.render();
console.log(client);