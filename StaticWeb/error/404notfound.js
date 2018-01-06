jQuery(function($)
{

    let
        GRAVITY = 2000,
        STARS_REQUIRED_FOR_RANDOM_ARTICLE = 3;

    /*let canvas = document.getElementById('easter-egg');
    let ctx = canvas.getContext('2d');*/

    let starsCollected = 0;

    let game = new Phaser.Game(600, 200, Phaser.AUTO, 'easter-egg-container', {
        preload: preload,
        create: create,
        update: update
    });

    function preload()
    {
        game.load.baseURL = '/error/easter-egg/assets/';
        game.load.image('spike', 'spike.png');
        game.load.image('star', 'star.png');
        game.load.image('404', '404sprite.png');
    }

    let player, obstacles, stars, cursors, starsCollectedText, redOverlayGraphics;

    function create()
    {
        cursors = game.input.keyboard.createCursorKeys();

        game.stage.backgroundColor = '#eee';

        starsCollectedText = game.add.text(16, 16, "Stars: " + starsCollected, { fontSize: '16px', fill: '#000' });

        game.physics.startSystem(Phaser.Physics.ARCADE);

        obstacles = game.add.group();
        obstacles.enableBody = true;

        stars = game.add.group();
        stars.enableBody = true;

        player = game.add.sprite(10, 35, '404');

        game.physics.arcade.enable(player);

        player.body.immovable = true;
        player.body.gravity.y = GRAVITY;
        player.body.collideWorldBounds = true;

        for (let i = 3; i <= 6; i++)
        {
            spawnSpike(i * 180);
        }

        redOverlayGraphics = game.add.graphics(0, 0);
        redOverlayGraphics.beginFill(0xff0013, 0);
        redOverlayGraphics.drawRect(0, 0, game.world.width, game.world.height);

    }

    let redOverlayTimer = 0;

    function update()
    {

        redOverlayGraphics.clear();
        redOverlayGraphics.beginFill(0xff0013, redOverlayTimer);
        redOverlayGraphics.drawRect(0, 0, game.world.width, game.world.height);

        if (redOverlayTimer > 0)
            redOverlayTimer = Math.max(0, redOverlayTimer - 0.1);

        // Handle collisions
        game.physics.arcade.collide(stars, obstacles);
        let hitObstacle = game.physics.arcade.collide(player, obstacles, (player, obstacle) =>
        {
            // Collision callback
            if (player.y <= obstacle.y && obstacle.___dir)
            {
                // Above downward-facing spike, can walk
                player.body.velocity.y = 0;
                return;
            }

            // Hit a spike! Remove stars as punishment
            starsCollected = Math.max(0, starsCollected - 2);
            updateStarsCollected();
            obstacle.destroy();
            spawnSpike(game.world.width - ((Math.random() - 0.5) * 100) + 50);
            // Overlay red rectangle for short amount of time
            redOverlayTimer = 1;
        });

        //
        obstacles.forEach(obstacle =>
        {
            function inRange(x, a, b)
            {
                return a <= x && x <= b;
            }

            let ox = obstacle.x, oy = obstacle.y;
            let px = player.x, py = player.y;
            if (obstacle.___dir && px + 2 >= ox && px <= ox + player.width + 2 && inRange(py, oy - 2, oy + 1))
            {
                // Allow walk
                player.body.velocity.y = 0;
            }
        });

        game.physics.arcade.overlap(player, stars, collectStar, null, this);

        let vy = player.body.velocity.y;

        // Only allow jump when
        //  - player on ground OR
        //  - continuous jump to (player height + 20)
        if (cursors.up.isDown && (vy === 0 || -300 <= vy && vy <= -250 && player.y > player.height + 20))
        {
            player.body.velocity.y = -300;
        }

        obstacles.forEach(obstacle =>
        {
            if (obstacle.x < -obstacle.width)
            {
                obstacle.destroy();
                // Spawn one spike for every destroyed one
                spawnSpike(game.world.width - ((Math.random() - 0.5) * 100) + 50);
            }
            else
                obstacle.body.velocity.x = -300;
        });

        stars.forEach(star =>
        {
            star.body.velocity.x = -280;
        });

        // Spawn stars quite rarely
        // A very low chance must be used because this is invoked EVERY FRAME
        if (Math.random() <= 0.003)
            spawnStar();

    }

    function spawnSpike(xOffset = 0)
    {

        let spike = obstacles.create(xOffset, game.world.height - 40, 'spike');
        spike.body.immovable = true;

        let direction = Math.random() < 0.5;
        if (direction)
        {
            spike.___dir = true;
            spike.anchor.setTo(0.5, 0.5);
            spike.scale.y *= -1;
            spike.y -= player.height + 15 + (Math.random() - 0.3) * 100;
        }

    }

    function spawnStar(xOffset = game.world.width)
    {

        let star = stars.create(xOffset, game.world.height - 100, 'star');
        star.body.collideWorldBounds = true;

    }

    function collectStar(player, star)
    {
        star.destroy();
        ++starsCollected;
        updateStarsCollected();
        if (starsCollected >= STARS_REQUIRED_FOR_RANDOM_ARTICLE)
        {
            window.location.href = '/random-article';
        }
    }

    function updateStarsCollected()
    {
        starsCollectedText.text = "Stars: " + starsCollected;
    }

});
