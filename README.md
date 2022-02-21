# mobafire-bot

software requirements

* git
* maven
* java 8

other requirements

viewbot:

* brightdata.com

votebot:

* proxiware.com
* anticaptcha

feel free to fork the project and modify the code if you want to use other proxy providers, this is a as-is dump of my source

to build the project run

```bash
git clone https://github.com/hawolt/mobafire-bot
cd mobafire-bot
bash setup.sh
```

once the project is build whitelist your ip on proxiware.com
after that configure `config.json` with your brightdata credentials for both the static and residential zone

your `config.json` should look similar to this

```json
{
  "bright-data-static-username": "lum-customer-abc-zone-static",
  "bright-data-static-password": "securepassword123",
  "bright-data-residential-hostname": "zproxy.lum-superproxy.io",
  "bright-data-residential-port": "22225"
}
```

once everything is setup run the bot using

```bash
bash run.sh
```
