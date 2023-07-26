
# CopyItem Plugin

CopyItem is a simple, yet customizable Spigot plugin that allows server operators and players with permission to copy items in their hand or in their sight.

## Features

-   **Item Duplication**: Copy any item in your hand or any block in your sight with a simple command.
-   **Custom Amounts**: Specify the amount of items you want to copy, up to a configurable maximum.
-   **Configurable**: From the maximum amount of items that can be copied to whether command usage gets announced to ops and in the terminal, everything is easily configurable.
-   **Messages Configuration**: All plugin messages are fully customizable.
-   **Permission Support**: Comes with permission nodes for using the copy item command and reloading the plugin.

## Commands

-   `/copyitem hand <amount>`: Copies the item in your hand.
-   `/copyitem sight <amount>`: Copies the item you're looking at.
-   `/copyitem reload`: Reloads the plugin configuration.
-   `/copyitem help`: Shows help message.

## Permissions
-   `copyitem.use`: Allows the use of the `/copyitem` command.
-   `copyitem.reload`: Allows the use of the `/copyitem reload` command.

## Configuration

**Config.yml**


    # Configuration file for the CopyItem plugin by Kaludi.
    #
    # 'announceCommandUsage': Whether to announce to the console and other operators when the /copyitem command is used.
    # 'maxCopyAmount': The maximum amount of items a player can copy at once.
    # 'sightMaxDistance': The maximum distance for the /copyitem sight command.
    #
    # After making changes to this file, save and do '/reload' or restart your server.
    
    settings:
      announceCommandUsage: true
      maxCopyAmount: 64
      sightMaxDistance: 100

**Messages.yml**

    messages:
      prefix: "&d&lCopyItem &2&l► &7"
      copiedSuccessfully: "&bCopied %s x%d successfully."
      noItemInHand: "&bNo item in hand to copy."
      noBlockInView: "&bNo block in sight to copy."
      invalidArgument: "&bInvalid argument."
      invalidNumberFormat: "&bInvalid number format for amount."
      invalidCommandUsage: "&bInvalid command usage. Use /copyitem hand/sight/help/reload <amount>."
      noPermission: "&bYou don't have permission to use this command."
      onlyPlayersCommand: "Only players can use this command."
      commandUsed: "Player %s used /copyitem command to copy %s x%d."
      configReloaded: "&bConfiguration reloaded successfully."
      copyitemHelp: "&bCopyItem Commands:\n/copyitem hand <amount> - Copies the item in your hand.\n/copyitem sight <amount> - Copies the item you're looking at.\n/copyitem reload - Reloads the plugin configuration.\n/copyitem help - Shows this help message."
