jQuery(function($)
{

    // Init "go back" link
    $('#go-back').css('cursor', 'pointer').click(() => window.history.back());

    // Constants
    // TODO: fine-tune values, extract more constants
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

    let
        /*
        The player sprite
        - player.x/y/width/height
        - player.body.velocity.x/y
         */
        player,
        /*
        A group containing all obstacles (right now only spikes).
        Colliding with a sprite in this group will count as "loosing"
        - obstacles.forEach(obstacle => obstacle.x/y/width/height/body.velocity.x/y)
         */
        obstacles,
        /* A group containing all stars
         * overlapping a sprite in this group will add 1 to collectedStars */
        stars,
        /* cursors.up/down/left/right.isDown corresponds to the arrow keys' status */
        cursors,
        /* starsCollectedText.text === "Stars: " + starsCollected
         * The setter will updated the actually displayed value */
        starsCollectedText,
        /* The Graphics object that is used to draw non-opaque red overlays
         * when colliding with an obstacle */
        redOverlayGraphics;

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

        // Add the player sprite, offset 10 x and 35 y, with the image '404'
        player = game.add.sprite(10, 35, '404');

        // Enable physics on player (so they are affected by gravity)
        game.physics.arcade.enable(player);

        // Player cannot be pushed around by objects
        player.body.immovable = true;
        // Player is accelerated towards bottom (positive y)
        player.body.gravity.y = GRAVITY;
        // Player collides with the world bounds, i.e. cannot fall through the ground
        player.body.collideWorldBounds = true;

        // Spawn a few obstacles (spikes) to get started
        for (let i = 3; i <= 6; i++)
        {
            spawnSpike(i * 180);
        }

        // Init redOverlayGraphics
        redOverlayGraphics = game.add.graphics(0, 0);
        redOverlayGraphics.beginFill(0xff0013, 0);
        redOverlayGraphics.drawRect(0, 0, game.world.width, game.world.height);

    }

    /* 0 <= redOverlayTimer <= 1
     * used as the value for the opaque-ness of the red overlay (see redOverlayGraphics) */
    let redOverlayTimer = 0;

    function update()
    {

        redOverlayGraphics.clear();
        redOverlayGraphics.beginFill(0xff0013, redOverlayTimer);
        redOverlayGraphics.drawRect(0, 0, game.world.width, game.world.height);

        // Decrement redOverlayTimer in every frame
        if (redOverlayTimer > 0)
            redOverlayTimer = Math.max(0, redOverlayTimer - 0.1);

        // Handle collisions
        // Stars can't pass through obstacles
        game.physics.arcade.collide(stars, obstacles);
        // Player hit any obstacle
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

        // Loop through every obstacle (spike)
        // if the player is walking on one (with a threshhold),
        // set their y-velocity to 0 so they can walk on it and jump
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

        // Check if player overlaps any star (any sprite in the stars group)
        // if so, run collectStar
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
            // Destroy star if off-screen
            if (star.x < -star.width)
                star.destroy();
            else
                star.body.velocity.x = -280;
        });

        // Spawn stars quite rarely
        // A very low chance must be used because this is invoked EVERY FRAME
        if (Math.random() <= 0.01)
            spawnStar();

    }

    /* Spawns a spike at the specified x-offset, randomly facing up or down */
    function spawnSpike(xOffset = 0)
    {

        // The spike must be part of the obstacles group
        // The sprite is 'spike'
        let spike = obstacles.create(xOffset, game.world.height - 40, 'spike');
        spike.body.immovable = true;

        // Randomly face up or down
        // A positive value indicates facing toward the ground
        let direction = Math.random() < 0.5;
        if (direction)
        {
            spike.___dir = true;
            // Flip image
            spike.anchor.setTo(0.5, 0.5);
            spike.scale.y *= -1;
            // Move up by a slightly random amount (needs tweaking, see to-do notice above)
            spike.y -= player.height + 15 + (Math.random() - 0.3) * 100;
        }
        else
        {
            spike.___dir = false;
        }

    }

    function spawnStar(xOffset = game.world.width)
    {

        let star = stars.create(xOffset, game.world.height - 100, 'star');

    }

    function collectStar(player, star)
    {
        star.destroy();
        ++starsCollected;
        updateStarsCollected();
        if (starsCollected === STARS_REQUIRED_FOR_RANDOM_ARTICLE)
        {
            window.location.href = '/random_article';
        }
    }

    function updateStarsCollected()
    {
        starsCollectedText.text = "Stars: " + starsCollected;
    }

});
