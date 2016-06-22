package me.reminisce.server

object JsonEntity {
  val game: (String, String, String) => String = (id: String, uid1: String, uid2: String) => s"""{
  "_id": "$id",
  "player1": "$uid1",
  "player2": "$uid2",

  "player1Board": {
    "userId": "MmYXQ5EKSgdjzP3uJ",
    "tiles": [
      {
        "_id": "4864c0d2194911e32fa3d133",
        "type": "Timeline",
        "question1": {
          "subject": {
            "text": "John Doe updated his profile picture.",
            "imageUrl": "https://scontent.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/254017_10150336272904968_4098495_n.jpg?oh=459f22a58ac86d7a7477fdd7d1b1fa19&oe=572DC12F",
            "facebookImageUrl": "https://www.facebook.com/photo.php?fbid=10150336272904968&set=a.499536429967.288132.656209967&type=3",
            "type": "ImagePost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "min": "2010-06-08T10:24:34+0000",
          "max": "2014-06-08T10:24:34+0000",
          "default": "2012-06-08T10:24:34+0000",
          "unit": "Year",
          "step": 1,
          "threshold": 0,
          "answer": "2011-06-08T10:24:34+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question2": {
          "subject": {
            "text": "is preparing to leave japan",
            "type": "TextPost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "min": "2006-05-01T22:27:38+0000",
          "max": "2010-05-01T22:27:38+0000",
          "default": "2007-05-01T22:27:38+0000",
          "unit": "Year",
          "step": 1,
          "threshold": 0,
          "answer": "2009-05-01T22:27:38+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question3": {
          "subject": {
            "text": "Thank you all for your congratulations and remember me!\nI also wish you all the best and hope i can see you all soon again!",
            "type": "TextPost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "min": "2008-09-04T20:54:09+0000",
          "max": "2012-05-04T20:54:09+0000",
          "default": "2008-09-04T20:54:09+0000",
          "unit": "Month",
          "step": 11,
          "threshold": 0,
          "answer": "2012-05-04T20:54:09+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "score": 2,
        "answered": true,
        "disabled": false
      },
      {
        "_id": "63a04b71d03fdec8ea0f5a10",
        "type": "Timeline",
        "question1": {
          "subject": {
            "text": "John Doe is playing Bandenkrieg.",
            "type": "TextPost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "min": "2007-01-08T10:05:20+0000",
          "max": "2011-01-08T10:05:20+0000",
          "default": "2011-01-08T10:05:20+0000",
          "unit": "Year",
          "step": 1,
          "threshold": 0,
          "answer": "2009-01-08T10:05:20+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question2": {
          "subject": {
            "text": "John Doe updated his cover photo.",
            "imageUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-0/p180x540/599960_10151324333344968_1397596433_n.jpg?oh=fa4747f3aba457a70871bd091b25c0a2&oe=572881ED",
            "facebookImageUrl": "https://www.facebook.com/photo.php?fbid=10151324333344968&set=a.10151324333279968.566984.656209967&type=3",
            "type": "ImagePost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "min": "2011-09-27T19:11:45+0000",
          "max": "2015-01-27T19:11:45+0000",
          "default": "2011-09-27T19:11:45+0000",
          "unit": "Month",
          "step": 10,
          "threshold": 0,
          "answer": "2012-07-27T19:11:45+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question3": {
          "subject": {
            "text": "John Doe added 2 new photos to the album: EPFL work.",
            "imageUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/s720x720/383857_10150714193384968_538147790_n.jpg?oh=7f1681d33a1198e8783db431de165186&oe=572BE0EB",
            "facebookImageUrl": "https://www.facebook.com/photo.php?fbid=10150714193384968&set=a.10150714192469968.500949.656209967&type=3",
            "type": "ImagePost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "min": "2009-01-15T22:15:26+0000",
          "max": "2013-01-15T22:15:26+0000",
          "default": "2013-01-15T22:15:26+0000",
          "unit": "Year",
          "step": 1,
          "threshold": 0,
          "answer": "2012-01-15T22:15:26+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "score": 1,
        "answered": true,
        "disabled": false
      },
      {
        "_id": "68c971bef45ff0c3021bfd21",
        "type": "Timeline",
        "question1": {
          "subject": {
            "name": "STIL",
            "pageId": "559848137434099",
            "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xat1/v/t1.0-9/p720x720/10686691_788026987949545_1958465264061110370_n.png?oh=36322c3c137ac93010d8231fcf6d9f0e&oe=575975D1",
            "type": "Page"
          },
          "min": "2015-03-11T15:34:56+0000",
          "max": "2015-11-11T15:34:56+0000",
          "default": "2015-11-11T15:34:56+0000",
          "unit": "Month",
          "step": 2,
          "threshold": 0,
          "answer": "2015-03-11T15:34:56+0000",
          "type": "TLWhenDidYouLikeThisPage",
          "kind": "Timeline"
        },
        "question2": {
          "subject": {
            "text": "Can't believe this myth is still alive...\nJohn Doe at Ecole polytechnique fédérale de Lausanne (EPFL).",
            "thumbnailUrl": "https://external.xx.fbcdn.net/safe_image.php?d=AQCSnqSk520YZiW4&w=720&h=720&url=https%3A%2F%2Fi.imgur.com%2F3luJoed.png&cfs=1",
            "url": "http://redditpics.fpapps.com/?thingid=t3_3dn0i9&url=https%3A%2F%2Fi.imgur.com%2F3luJoed.png",
            "type": "LinkPost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "min": "2015-05-17T23:46:43+0000",
          "max": "2015-09-17T23:46:43+0000",
          "default": "2015-06-17T23:46:43+0000",
          "unit": "Month",
          "step": 1,
          "threshold": 0,
          "answer": "2015-07-17T23:46:43+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question3": {
          "subject": {
            "text": "Well not all of Asia but at least some of it.\nJohn Doe added 33 new photos to the album: Asia.",
            "imageUrl": "https://scontent.xx.fbcdn.net/hphotos-xta1/v/t1.0-9/1936047_125039684967_8160918_n.jpg?oh=5c3c491f1a8e24926abb08c11fa9f50f&oe=57669F72",
            "facebookImageUrl": "https://www.facebook.com/photo.php?fbid=125039684967&set=a.125037344967.128095.656209967&type=3",
            "type": "ImagePost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "min": "2005-07-06T00:02:16+0000",
          "max": "2009-07-06T00:02:16+0000",
          "default": "2005-07-06T00:02:16+0000",
          "unit": "Year",
          "step": 1,
          "threshold": 0,
          "answer": "2009-07-06T00:02:16+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "score": 3,
        "answered": true,
        "disabled": false
      },
      {
        "_id": "e764fa2e317a2d752d5d092e",
        "type": "Order",
        "question1": {
          "choices": [
            {
              "subject": {
                "name": "Perfume",
                "pageId": "108508312507532",
                "type": "Page"
              },
              "uId": 2
            },
            {
              "subject": {
                "name": "BuzzFeed",
                "pageId": "21898300328",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xlt1/v/t1.0-9/12341195_10154146068075329_1116187457473879896_n.jpg?oh=007bc5343140f4b04f03efd6f302d0da&oe=576E2CEC",
                "type": "Page"
              },
              "uId": 1
            },
            {
              "subject": {
                "name": "One Man Left",
                "pageId": "189119219677",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/18791_10153183626589678_3265181831346378838_n.png?oh=66f66a1f7c92e6b34c3587387363f93a&oe=572D6A22",
                "type": "Page"
              },
              "uId": 0
            }
          ],
          "items": [
            {
              "id": 2,
              "text": "Perfume",
              "subject": {
                "name": "Perfume",
                "pageId": "108508312507532",
                "type": "Page"
              }
            },
            {
              "id": 1,
              "text": "BuzzFeed",
              "subject": {
                "name": "BuzzFeed",
                "pageId": "21898300328",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xlt1/v/t1.0-9/12341195_10154146068075329_1116187457473879896_n.jpg?oh=007bc5343140f4b04f03efd6f302d0da&oe=576E2CEC",
                "type": "Page"
              }
            },
            {
              "id": 0,
              "text": "One Man Left",
              "subject": {
                "name": "One Man Left",
                "pageId": "189119219677",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/18791_10153183626589678_3265181831346378838_n.png?oh=66f66a1f7c92e6b34c3587387363f93a&oe=572D6A22",
                "type": "Page"
              }
            }
          ],
          "answer": [
            0,
            2,
            1
          ],
          "type": "ORDPageLikes",
          "kind": "Order"
        },
        "question2": {
          "choices": [
            {
              "subject": {
                "name": "Theatersport Improphil Luzern",
                "pageId": "21453913723",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/t31.0-8/s720x720/10917947_10153046619088724_896737706453526597_o.jpg",
                "type": "Page"
              },
              "uId": 1
            },
            {
              "subject": {
                "name": "Code School",
                "pageId": "172873029427580",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xlp1/v/t1.0-9/p720x720/12390833_946063258775216_1250853723852053504_n.png?oh=90cecc446a0d5a74b4426dc25c6ef0cf&oe=57235F1C",
                "type": "Page"
              },
              "uId": 2
            },
            {
              "subject": {
                "name": "Archon",
                "pageId": "62898946203",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/1927604_62912876203_6515_n.jpg?oh=89b4620e47313dba44b9879c4eeb7ab7&oe=5727D39D",
                "type": "Page"
              },
              "uId": 0
            }
          ],
          "items": [
            {
              "id": 1,
              "text": "Theatersport Improphil Luzern",
              "subject": {
                "name": "Theatersport Improphil Luzern",
                "pageId": "21453913723",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/t31.0-8/s720x720/10917947_10153046619088724_896737706453526597_o.jpg",
                "type": "Page"
              }
            },
            {
              "id": 2,
              "text": "Code School",
              "subject": {
                "name": "Code School",
                "pageId": "172873029427580",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xlp1/v/t1.0-9/p720x720/12390833_946063258775216_1250853723852053504_n.png?oh=90cecc446a0d5a74b4426dc25c6ef0cf&oe=57235F1C",
                "type": "Page"
              }
            },
            {
              "id": 0,
              "text": "Archon",
              "subject": {
                "name": "Archon",
                "pageId": "62898946203",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/1927604_62912876203_6515_n.jpg?oh=89b4620e47313dba44b9879c4eeb7ab7&oe=5727D39D",
                "type": "Page"
              }
            }
          ],
          "answer": [
            0,
            1,
            2
          ],
          "type": "ORDPageLikes",
          "kind": "Order"
        },
        "question3": {
          "choices": [
            {
              "subject": {
                "name": "Theater Aeternam",
                "pageId": "294888807407",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xtp1/t31.0-8/q87/s720x720/11095508_10153082103052408_6415046157432984551_o.jpg",
                "type": "Page"
              },
              "uId": 1
            },
            {
              "subject": {
                "name": "Stack Overflow",
                "pageId": "11239244970",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xft1/v/t1.0-9/12006269_10150553545389971_5448097255504457554_n.png?oh=fb6e7710991ecafd542116ea1394c3ae&oe=5726C4B6",
                "type": "Page"
              },
              "uId": 0
            },
            {
              "subject": {
                "name": "reddit",
                "pageId": "7177913734",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xla1/v/t1.0-9/p720x720/12247068_10153747241073735_792837519389065419_n.png?oh=2dd1a3d4e10a463e9cafe86e5514a3ce&oe=5770667C",
                "type": "Page"
              },
              "uId": 2
            }
          ],
          "items": [
            {
              "id": 1,
              "text": "Theater Aeternam",
              "subject": {
                "name": "Theater Aeternam",
                "pageId": "294888807407",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xtp1/t31.0-8/q87/s720x720/11095508_10153082103052408_6415046157432984551_o.jpg",
                "type": "Page"
              }
            },
            {
              "id": 0,
              "text": "Stack Overflow",
              "subject": {
                "name": "Stack Overflow",
                "pageId": "11239244970",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xft1/v/t1.0-9/12006269_10150553545389971_5448097255504457554_n.png?oh=fb6e7710991ecafd542116ea1394c3ae&oe=5726C4B6",
                "type": "Page"
              }
            },
            {
              "id": 2,
              "text": "reddit",
              "subject": {
                "name": "reddit",
                "pageId": "7177913734",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xla1/v/t1.0-9/p720x720/12247068_10153747241073735_792837519389065419_n.png?oh=2dd1a3d4e10a463e9cafe86e5514a3ce&oe=5770667C",
                "type": "Page"
              }
            }
          ],
          "answer": [
            1,
            0,
            2
          ],
          "type": "ORDPageLikes",
          "kind": "Order"
        },
        "score": 1,
        "answered": true,
        "disabled": false
      },
      {
        "_id": "8c5cc8ae38a6233069d469d5",
        "type": "MultipleChoice",
        "question1": {
          "subject": {
            "text": "John Doe shared a link.",
            "thumbnailUrl": "https://external.xx.fbcdn.net/safe_image.php?d=AQBgK6vGG5nYP2hm&w=720&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FfzMhh8zhTiY%2F0.jpg&cfs=1",
            "url": "http://www.youtube.com/watch?v=fzMhh8zhTiY",
            "type": "VideoPost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "choices": [
            {
              "text": "Ramona Pauchard-Batschulat",
              "imageUrl": null,
              "fbId": "10205452190845166",
              "pageId": null
            },
            {
              "text": "Sacha Vost",
              "imageUrl": null,
              "fbId": "1004436936250140",
              "pageId": null
            },
            {
              "text": "Alex Coudray",
              "imageUrl": null,
              "fbId": "10205299210212844",
              "pageId": null
            },
            {
              "text": "Francois Riv",
              "imageUrl": null,
              "fbId": "10152761449809191",
              "pageId": null
            }
          ],
          "answer": 3,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "question2": {
          "subject": {
            "text": "Don't you just love those beta oxidation potentials, i mean as long as they contain the capability to produce the substance which will help the organism which carries chromosomes which are partially identical to yours, to create a proton gradient?",
            "type": "TextPost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "choices": [
            {
              "text": "Alex Coudray",
              "imageUrl": null,
              "fbId": "10205299210212844",
              "pageId": null
            },
            {
              "text": "Edoardo D'Anna",
              "imageUrl": null,
              "fbId": "10152932359394244",
              "pageId": null
            },
            {
              "text": "Rose Anna K",
              "imageUrl": null,
              "fbId": "10152625640621482",
              "pageId": null
            },
            {
              "text": "Aline Zed",
              "imageUrl": null,
              "fbId": "10204451423618259",
              "pageId": null
            }
          ],
          "answer": 1,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "question3": {
          "subject": {
            "text": "Well probably not many will get this...\nJohn Doe shared Trust Me, I'm an Engineer's photo.",
            "imageUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/643883_10151178412373360_874707777_n.jpg?oh=29c8d99f896438f4b93c7577bcabb889&oe=57621114",
            "facebookImageUrl": "https://www.facebook.com/trustmestore/photos/a.379473193359.158222.290539813359/10151178412373360/?type=3",
            "type": "ImagePost",
            "from": {
              "userId": "656209967",
              "userName": "John Doe"
            }
          },
          "choices": [
            {
              "text": "Ramona Pauchard-Batschulat",
              "imageUrl": null,
              "fbId": "10205452190845166",
              "pageId": null
            },
            {
              "text": "Oswald Maskens",
              "imageUrl": null,
              "fbId": "10203344536206575",
              "pageId": null
            },
            {
              "text": "George Stefanidis",
              "imageUrl": null,
              "fbId": "10152712496469635",
              "pageId": null
            },
            {
              "text": "Dennys Kuhnert",
              "imageUrl": null,
              "fbId": "757293011",
              "pageId": null
            }
          ],
          "answer": 3,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "score": 0,
        "answered": true,
        "disabled": false
      }
    ],
    "_id": "wQz5dAXnpDcyvrJud"
  },
  "player2Board": {
    "userId": "wucQxKHvs5W9Ao9y9",
    "tiles": [
      {
        "_id": "87c565b56f7fc92ff8617c2b",
        "type": "Timeline",
        "question1": {
          "subject": {
            "text": "Its so hot in switzerland, I even need a air conditioner\\\n",
            "type": "TextPost"
          },
          "min": "2015-06-03T11:41:09+0000",
          "max": "2015-06-07T11:41:09+0000",
          "default": "2015-06-03T11:41:09+0000",
          "unit": "Day",
          "step": 1,
          "threshold": 0,
          "answer": "2015-06-07T11:41:09+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question2": {
          "subject": {
            "text": "Working hard on my semester project\\\n",
            "type": "TextPost"
          },
          "min": "2015-06-03T11:39:41+0000",
          "max": "2015-06-07T11:39:41+0000",
          "default": "2015-06-03T11:39:41+0000",
          "unit": "Day",
          "step": 1,
          "threshold": 0,
          "answer": "2015-06-07T11:39:41+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question3": {
          "subject": {
            "text": "\\\nJohn Doe shared a link.",
            "thumbnailUrl": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQD7rH6nk2VQdben&w=720&h=720&url=https%3A%2F%2Ffbcdn-sphotos-d-a.akamaihd.net%2Fhphotos-ak-xap1%2Fv%2Ft1.0-9%2Fs720x720%2F10991257_10155264188050078_8267303349379896564_n.png%3Foh%3Deabcf538999a30b02a9cf23502293cf0%26oe%3D559548F5%26__gda__%3D1431635790_94e1263c935aad9bb1662dcfd812475b&cfs=1",
            "url": "http://theoatmeal.com/comics/cats_actually_kill",
            "type": "LinkPost"
          },
          "min": "2014-11-17T23:12:50+0000",
          "max": "2015-03-17T23:12:50+0000",
          "default": "2014-11-17T23:12:50+0000",
          "unit": "Month",
          "step": 1,
          "threshold": 0,
          "answer": "2015-02-17T23:12:50+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      },
      {
        "_id": "a70c304251a99bd8d70bce2e",
        "type": "Order",
        "question1": {
          "choices": [
            {
              "subject": {
                "name": "Blood Bowl",
                "pageId": "13590131663",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0",
                "type": "Page"
              },
              "uId": 0
            },
            {
              "subject": {
                "name": "Archon",
                "pageId": "62898946203",
                "photoUrl": "https://fbcdn-sphotos-b-a.akamaihd.net/hphotos-ak-xfa1/v/t1.0-9/1927604_62912876203_6515_n.jpg?oh=04f527988aafdb96ec94e3eedb1acfdf&oe=55EB6B9D&__gda__=1442425373_549246737f279e2b8573a0f794e02981",
                "type": "Page"
              },
              "uId": 2
            },
            {
              "subject": {
                "name": "Heroes of Newerth",
                "pageId": "63037549101",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/404113_10151126222244102_1551661475_n.jpg?oh=aeb626c286ce72c97c36280d75ead230&oe=5600861B",
                "type": "Page"
              },
              "uId": 1
            }
          ],
          "items": [
            {
              "id": 0,
              "text": "Blood Bowl",
              "subject": {
                "name": "Blood Bowl",
                "pageId": "13590131663",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0",
                "type": "Page"
              }
            },
            {
              "id": 2,
              "text": "Archon",
              "subject": {
                "name": "Archon",
                "pageId": "62898946203",
                "photoUrl": "https://fbcdn-sphotos-b-a.akamaihd.net/hphotos-ak-xfa1/v/t1.0-9/1927604_62912876203_6515_n.jpg?oh=04f527988aafdb96ec94e3eedb1acfdf&oe=55EB6B9D&__gda__=1442425373_549246737f279e2b8573a0f794e02981",
                "type": "Page"
              }
            },
            {
              "id": 1,
              "text": "Heroes of Newerth",
              "subject": {
                "name": "Heroes of Newerth",
                "pageId": "63037549101",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/404113_10151126222244102_1551661475_n.jpg?oh=aeb626c286ce72c97c36280d75ead230&oe=5600861B",
                "type": "Page"
              }
            }
          ],
          "answer": [
            2,
            0,
            1
          ],
          "type": "ORDPageLikeTime",
          "kind": "Order"
        },
        "question2": {
          "choices": [
            {
              "subject": {
                "name": "Overwatch",
                "pageId": "709571559091726",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/10407070_732714740110741_588645306290663759_n.png?oh=95bbc1c8263d6b3cf8afbcabf86f5990&oe=55F32E77",
                "type": "Page"
              },
              "uId": 1
            },
            {
              "subject": {
                "name": "Hackers at EPFL",
                "pageId": "295515750564317",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xtp1/v/t1.0-9/p720x720/154315_305877992861426_549885057_n.png?oh=cc6abea3a4be69d503592990a48a818e&oe=560351B7",
                "type": "Page"
              },
              "uId": 2
            },
            {
              "subject": {
                "name": "Association µBrasserie",
                "pageId": "801226016630881",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/v/t1.0-9/1551643_801227776630705_2877667107454862675_n.png?oh=aa2a0153908b43c29ca751077dbf9a31&oe=5605BD0C",
                "type": "Page"
              },
              "uId": 0
            }
          ],
          "items": [
            {
              "id": 1,
              "text": "Overwatch",
              "subject": {
                "name": "Overwatch",
                "pageId": "709571559091726",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/10407070_732714740110741_588645306290663759_n.png?oh=95bbc1c8263d6b3cf8afbcabf86f5990&oe=55F32E77",
                "type": "Page"
              }
            },
            {
              "id": 2,
              "text": "Hackers at EPFL",
              "subject": {
                "name": "Hackers at EPFL",
                "pageId": "295515750564317",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xtp1/v/t1.0-9/p720x720/154315_305877992861426_549885057_n.png?oh=cc6abea3a4be69d503592990a48a818e&oe=560351B7",
                "type": "Page"
              }
            },
            {
              "id": 0,
              "text": "Association µBrasserie",
              "subject": {
                "name": "Association µBrasserie",
                "pageId": "801226016630881",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/v/t1.0-9/1551643_801227776630705_2877667107454862675_n.png?oh=aa2a0153908b43c29ca751077dbf9a31&oe=5605BD0C",
                "type": "Page"
              }
            }
          ],
          "answer": [
            2,
            0,
            1
          ],
          "type": "ORDPageLikeTime",
          "kind": "Order"
        },
        "question3": {
          "choices": [
            {
              "subject": {
                "text": "I will post some weird things in the future, but its for science so be prepared to see some abnormal activity...\\\n",
                "type": "TextPost"
              },
              "uId": 2
            },
            {
              "subject": {
                "text": "Predestination is one of the few examples where the time travel paradox is well implemented...\\\n",
                "type": "TextPost"
              },
              "uId": 0
            },
            {
              "subject": {
                "text": "\\\nJohn Doe shared a link.",
                "thumbnailUrl": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQD7rH6nk2VQdben&w=720&h=720&url=https%3A%2F%2Ffbcdn-sphotos-d-a.akamaihd.net%2Fhphotos-ak-xap1%2Fv%2Ft1.0-9%2Fs720x720%2F10991257_10155264188050078_8267303349379896564_n.png%3Foh%3Deabcf538999a30b02a9cf23502293cf0%26oe%3D559548F5%26__gda__%3D1431635790_94e1263c935aad9bb1662dcfd812475b&cfs=1",
                "url": "http://theoatmeal.com/comics/cats_actually_kill",
                "type": "LinkPost"
              },
              "uId": 1
            }
          ],
          "items": [
            {
              "id": 2,
              "text": "I will post some weird things in the future, but its for science so be prepared to see some abnormal activity...\\\n",
              "subject": {
                "text": "I will post some weird things in the future, but its for science so be prepared to see some abnormal activity...\\\n",
                "type": "TextPost"
              }
            },
            {
              "id": 0,
              "text": "Predestination is one of the few examples where the time travel paradox is well implemented...\\\n",
              "subject": {
                "text": "Predestination is one of the few examples where the time travel paradox is well implemented...\\\n",
                "type": "TextPost"
              }
            },
            {
              "id": 1,
              "text": "\\\nJohn Doe shared a link.",
              "subject": {
                "text": "\\\nJohn Doe shared a link.",
                "thumbnailUrl": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQD7rH6nk2VQdben&w=720&h=720&url=https%3A%2F%2Ffbcdn-sphotos-d-a.akamaihd.net%2Fhphotos-ak-xap1%2Fv%2Ft1.0-9%2Fs720x720%2F10991257_10155264188050078_8267303349379896564_n.png%3Foh%3Deabcf538999a30b02a9cf23502293cf0%26oe%3D559548F5%26__gda__%3D1431635790_94e1263c935aad9bb1662dcfd812475b&cfs=1",
                "url": "http://theoatmeal.com/comics/cats_actually_kill",
                "type": "LinkPost"
              }
            }
          ],
          "answer": [
            1,
            0,
            2
          ],
          "type": "ORDPostLikesNumber",
          "kind": "Order"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      },
      {
        "_id": "15135bd8118b31fa2091ccdb",
        "type": "MultipleChoice",
        "question1": {
          "subject": {
            "text": "\\\nYou changed your profile picture.",
            "imageUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/v/t1.0-9/p180x540/396092_10151324342204968_433100355_n.jpg?oh=107c5dc045e438b5273a14d568c934c5&oe=55F0453B",
            "facebookImageUrl": "https://www.facebook.com/photo.php?fbid=10151324342204968&set=a.499536429967.288132.656209967&type=1",
            "type": "ImagePost"
          },
          "choices": [
            {
              "text": "Maria Maria",
              "imageUrl": null,
              "fbId": "10152584486929069",
              "pageId": null
            },
            {
              "text": "Zelal Al-Shemmery",
              "imageUrl": null,
              "fbId": "768569646537959",
              "pageId": null
            },
            {
              "text": "Christian M. Schmid",
              "imageUrl": null,
              "fbId": "10201396264188446",
              "pageId": null
            },
            {
              "text": "Michalina Pacholska",
              "imageUrl": null,
              "fbId": "714035445332109",
              "pageId": null
            }
          ],
          "answer": 1,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "question2": {
          "subject": {
            "text": "Next step: Rebuild a human by placing atoms at the correct locations\\\n",
            "thumbnailUrl": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQBiR3Xf13B7yRS6&w=720&h=720&url=http%3A%2F%2Fi4.ytimg.com%2Fvi%2FoSCX78-8-q0%2Fmaxresdefault.jpg%3Ffeature%3Dog&cfs=1",
            "type": "LinkPost"
          },
          "choices": [
            {
              "text": "Pascal Isenegger",
              "imageUrl": null,
              "fbId": "10153155485494466",
              "pageId": null
            },
            {
              "text": "Rose Anna K",
              "imageUrl": null,
              "fbId": "10152333475531482",
              "pageId": null
            },
            {
              "text": "Dennis van der Bij",
              "imageUrl": null,
              "fbId": "10204666335476227",
              "pageId": null
            },
            {
              "text": "Dennys Kuhnert",
              "imageUrl": null,
              "fbId": "10152763514958012",
              "pageId": null
            }
          ],
          "answer": 3,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "question3": {
          "subject": {
            "text": "Well probably not many will get this...\\\nJohn Doe shared Trust Me, I'm an \\\"Engineer\\\"'s photo.",
            "imageUrl": "https://fbcdn-sphotos-d-a.akamaihd.net/hphotos-ak-xfa1/v/t1.0-9/643883_10151178412373360_874707777_n.jpg?oh=fcc8301e3d77cfc4e8319fa0cdb4dfe9&oe=55FE1C14&__gda__=1442723875_0b0693ee9b00e79311a7c8a199083259",
            "facebookImageUrl": "https://www.facebook.com/trustmestore/photos/a.379473193359.158222.290539813359/10151178412373360/?type=1",
            "type": "ImagePost"
          },
          "choices": [
            {
              "text": "Aline Zed",
              "imageUrl": null,
              "fbId": "10203519324236357",
              "pageId": null
            },
            {
              "text": "Mark Cloostermans",
              "imageUrl": null,
              "fbId": "10152429814583577",
              "pageId": null
            },
            {
              "text": "Romy Hasler",
              "imageUrl": null,
              "fbId": "10204440016983177",
              "pageId": null
            },
            {
              "text": "Dennys Kuhnert",
              "imageUrl": null,
              "fbId": "10152763514958012",
              "pageId": null
            }
          ],
          "answer": 3,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      },
      {
        "_id": "312561225b42bfdd4daf9cf8",
        "type": "Order",
        "question1": {
          "choices": [
            {
              "subject": {
                "name": "Blood Bowl",
                "pageId": "13590131663",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0",
                "type": "Page"
              },
              "uId": 2
            },
            {
              "subject": {
                "name": "Heroes of Newerth",
                "pageId": "63037549101",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/404113_10151126222244102_1551661475_n.jpg?oh=aeb626c286ce72c97c36280d75ead230&oe=5600861B",
                "type": "Page"
              },
              "uId": 1
            },
            {
              "subject": {
                "name": "Archon",
                "pageId": "62898946203",
                "photoUrl": "https://fbcdn-sphotos-b-a.akamaihd.net/hphotos-ak-xfa1/v/t1.0-9/1927604_62912876203_6515_n.jpg?oh=04f527988aafdb96ec94e3eedb1acfdf&oe=55EB6B9D&__gda__=1442425373_549246737f279e2b8573a0f794e02981",
                "type": "Page"
              },
              "uId": 0
            }
          ],
          "items": [
            {
              "id": 2,
              "text": "Blood Bowl",
              "subject": {
                "name": "Blood Bowl",
                "pageId": "13590131663",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0",
                "type": "Page"
              }
            },
            {
              "id": 1,
              "text": "Heroes of Newerth",
              "subject": {
                "name": "Heroes of Newerth",
                "pageId": "63037549101",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/404113_10151126222244102_1551661475_n.jpg?oh=aeb626c286ce72c97c36280d75ead230&oe=5600861B",
                "type": "Page"
              }
            },
            {
              "id": 0,
              "text": "Archon",
              "subject": {
                "name": "Archon",
                "pageId": "62898946203",
                "photoUrl": "https://fbcdn-sphotos-b-a.akamaihd.net/hphotos-ak-xfa1/v/t1.0-9/1927604_62912876203_6515_n.jpg?oh=04f527988aafdb96ec94e3eedb1acfdf&oe=55EB6B9D&__gda__=1442425373_549246737f279e2b8573a0f794e02981",
                "type": "Page"
              }
            }
          ],
          "answer": [
            0,
            2,
            1
          ],
          "type": "ORDPageLike",
          "kind": "Order"
        },
        "question2": {
          "choices": [
            {
              "subject": {
                "name": "Jessica Chobot (Fans)",
                "pageId": "8865589402",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-frc3/v/t1.0-9/535735_10150711363104403_1659456882_n.jpg?oh=128a012c19424d59506bc943fd6ea493&oe=5631DEAB",
                "type": "Page"
              },
              "uId": 2
            },
            {
              "subject": {
                "name": "World of Warcraft",
                "pageId": "209989148221",
                "photoUrl": "https://fbcdn-sphotos-b-a.akamaihd.net/hphotos-ak-xap1/v/t1.0-9/10488215_10152975804013222_7875338501155809001_n.jpg?oh=6f308a2dd33765d8f3a5928a172eda02&oe=55FEE8A5&__gda__=1445962224_c12e25ab289b5b17b75c176c2f42efca",
                "type": "Page"
              },
              "uId": 0
            },
            {
              "subject": {
                "name": "DJ Lubel",
                "pageId": "68432448695",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/167151_10150158805113696_2646180_n.jpg?oh=d893250651d61507921b4a2c9d3cf2e6&oe=560706F7",
                "type": "Page"
              },
              "uId": 1
            }
          ],
          "items": [
            {
              "id": 2,
              "text": "Jessica Chobot (Fans)",
              "subject": {
                "name": "Jessica Chobot (Fans)",
                "pageId": "8865589402",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-frc3/v/t1.0-9/535735_10150711363104403_1659456882_n.jpg?oh=128a012c19424d59506bc943fd6ea493&oe=5631DEAB",
                "type": "Page"
              }
            },
            {
              "id": 0,
              "text": "World of Warcraft",
              "subject": {
                "name": "World of Warcraft",
                "pageId": "209989148221",
                "photoUrl": "https://fbcdn-sphotos-b-a.akamaihd.net/hphotos-ak-xap1/v/t1.0-9/10488215_10152975804013222_7875338501155809001_n.jpg?oh=6f308a2dd33765d8f3a5928a172eda02&oe=55FEE8A5&__gda__=1445962224_c12e25ab289b5b17b75c176c2f42efca",
                "type": "Page"
              }
            },
            {
              "id": 1,
              "text": "DJ Lubel",
              "subject": {
                "name": "DJ Lubel",
                "pageId": "68432448695",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/167151_10150158805113696_2646180_n.jpg?oh=d893250651d61507921b4a2c9d3cf2e6&oe=560706F7",
                "type": "Page"
              }
            }
          ],
          "answer": [
            2,
            1,
            0
          ],
          "type": "ORDPageLike",
          "kind": "Order"
        },
        "question3": {
          "choices": [
            {
              "subject": {
                "name": "BattleForge",
                "pageId": "20934494052",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/32298_10151154930419053_1354682688_n.png?oh=6df00e0cc36c3b494bda85aa7b3e01ad&oe=56331646",
                "type": "Page"
              },
              "uId": 0
            },
            {
              "subject": {
                "name": "Blood Bowl",
                "pageId": "47065585227",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/1911640_10153912593745228_143640444_n.jpg?oh=9f4c5aeb368e2c9beeadc5cdd1573de8&oe=55F22095",
                "type": "Page"
              },
              "uId": 2
            },
            {
              "subject": {
                "name": "Giant Bomb",
                "pageId": "23874020273",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/v/l/t1.0-9/394592_10151979521835274_497828467_n.jpg?oh=857f79c63279c57ac8189660c9b5d2d0&oe=55F95463",
                "type": "Page"
              },
              "uId": 1
            }
          ],
          "items": [
            {
              "id": 0,
              "text": "BattleForge",
              "subject": {
                "name": "BattleForge",
                "pageId": "20934494052",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/32298_10151154930419053_1354682688_n.png?oh=6df00e0cc36c3b494bda85aa7b3e01ad&oe=56331646",
                "type": "Page"
              }
            },
            {
              "id": 2,
              "text": "Blood Bowl",
              "subject": {
                "name": "Blood Bowl",
                "pageId": "47065585227",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/1911640_10153912593745228_143640444_n.jpg?oh=9f4c5aeb368e2c9beeadc5cdd1573de8&oe=55F22095",
                "type": "Page"
              }
            },
            {
              "id": 1,
              "text": "Giant Bomb",
              "subject": {
                "name": "Giant Bomb",
                "pageId": "23874020273",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/v/l/t1.0-9/394592_10151979521835274_497828467_n.jpg?oh=857f79c63279c57ac8189660c9b5d2d0&oe=55F95463",
                "type": "Page"
              }
            }
          ],
          "answer": [
            1,
            0,
            2
          ],
          "type": "ORDPageLike",
          "kind": "Order"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      },
      {
        "_id": "9c4495515207c4c6ffbee8d8",
        "type": "Geolocation",
        "question1": {
          "subject": {
            "text": "Well working at my lab\\\nJohn Doe at Ecole Polytechnique Fédérale de Lausanne (EPFL)",
            "imageUrl": "https://scontent.xx.fbcdn.net/hphotos-xpf1/v/t1.0-9/s720x720/10444531_10153433116934968_432924540316669439_n.jpg?oh=116626e635444a1b03895fab66efabc1&oe=55FC9749",
            "facebookImageUrl": "https://www.facebook.com/photo.php?fbid=10153433116934968&set=a.10153433118449968.1073741825.656209967&type=1",
            "type": "ImagePost"
          },
          "range": 0.02612831795,
          "defaultLocation": {
            "latitude": 46.54730608686859,
            "longitude": 6.57538616738275
          },
          "answer": {
            "latitude": 46.519681242464,
            "longitude": 6.5717116820427
          },
          "type": "GeoWhatCoordinatesWereYouAt",
          "kind": "Geolocation"
        },
        "question2": {
          "subject": {
            "text": "Working hard on my semester project\\\n",
            "type": "TextPost"
          },
          "range": 0.02612831795,
          "defaultLocation": {
            "latitude": 46.49610463062113,
            "longitude": 6.592828067959093
          },
          "answer": {
            "latitude": 46.519681242464,
            "longitude": 6.5717116820427
          },
          "type": "GeoWhatCoordinatesWereYouAt",
          "kind": "Geolocation"
        },
        "question3": {
          "subject": {
            "text": "Its so hot in switzerland, I even need a air conditioner\\\n",
            "type": "TextPost"
          },
          "range": 0.02612831795,
          "defaultLocation": {
            "latitude": 46.5414691078216,
            "longitude": 6.6166424020690755
          },
          "answer": {
            "latitude": 46.5198,
            "longitude": 6.6335
          },
          "type": "GeoWhatCoordinatesWereYouAt",
          "kind": "Geolocation"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      },
      {
        "_id": "fae93f1cfe21277316d2d6df",
        "type": "Timeline",
        "question1": {
          "subject": {
            "text": "Just bought http://www.manning.com/bernhardt/ can't wait until I will be able to test out play again with the web frontend beeing reactive\\\n",
            "thumbnailUrl": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQCyz2hQVNWsVlI-&w=720&h=720&url=http%3A%2F%2Fmanning.com%2Fimages%2Flogo.gif&cfs=1",
            "url": "http://www.manning.com/bernhardt/",
            "type": "LinkPost"
          },
          "min": "2015-06-03T12:04:57+0000",
          "max": "2015-06-07T12:04:57+0000",
          "default": "2015-06-03T12:04:57+0000",
          "unit": "Day",
          "step": 1,
          "threshold": 0,
          "answer": "2015-06-07T12:04:57+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question2": {
          "subject": {
            "text": "All this posts are just to get my master project working\\\n",
            "type": "TextPost"
          },
          "min": "2015-06-03T11:42:41+0000",
          "max": "2015-06-07T11:42:41+0000",
          "default": "2015-06-03T11:42:41+0000",
          "unit": "Day",
          "step": 1,
          "threshold": 0,
          "answer": "2015-06-07T11:42:41+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "question3": {
          "subject": {
            "text": "Soon I will be close to finishing all my major master classes and obtain nearly 90% of all my credits I need\\\n",
            "type": "TextPost"
          },
          "min": "2015-06-03T11:42:04+0000",
          "max": "2015-06-07T11:42:04+0000",
          "default": "2015-06-03T11:42:04+0000",
          "unit": "Day",
          "step": 1,
          "threshold": 0,
          "answer": "2015-06-07T11:42:04+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      },
      {
        "_id": "4e91dbcaee444b92bb187e8d",
        "type": "Misc",
        "question1": {
          "subject": {
            "comment": "I just noticed that. How is it scientific if it's not your usual and natural behavior ? ;)",
            "post": {
              "text": "I will post some weird things in the future, but its for science so be prepared to see some abnormal activity...\\\n",
              "type": "TextPost"
            },
            "type": "Comment"
          },
          "choices": [
            {
              "text": "George Stefanidis",
              "imageUrl": null,
              "fbId": "10152164136899635",
              "pageId": null
            },
            {
              "text": "Bühler Pascal",
              "imageUrl": null,
              "fbId": "10152329532881380",
              "pageId": null
            },
            {
              "text": "John Doe",
              "imageUrl": null,
              "fbId": "10153179507419968",
              "pageId": null
            },
            {
              "text": "Dennys Kuhnert",
              "imageUrl": null,
              "fbId": "10152763514958012",
              "pageId": null
            }
          ],
          "answer": 3,
          "type": "MCWhoMadeThisCommentOnYourPost",
          "kind": "MultipleChoice"
        },
        "question2": {
          "choices": [
            {
              "subject": {
                "name": "Coaching EPFL",
                "pageId": "274073285953982",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/10311773_927077923986845_3403559294023312734_n.jpg?oh=77202705c55c9b74dfbc3f0f0efb1a6a&oe=563082A2",
                "type": "Page"
              },
              "uId": 1
            },
            {
              "subject": {
                "name": "Zwischenbühne Horw",
                "pageId": "330485973712339",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-frc3/v/t1.0-9/1234869_493707037390231_1390182891_n.jpg?oh=50305a1fca17212f9f2ce3ed823a4bb0&oe=562F7DE6",
                "type": "Page"
              },
              "uId": 2
            },
            {
              "subject": {
                "name": "spood.me",
                "pageId": "229483891391",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/t31.0-8/s720x720/1978503_10152795480756392_1133562003378314118_o.png",
                "type": "Page"
              },
              "uId": 0
            }
          ],
          "items": [
            {
              "id": 1,
              "text": "Coaching EPFL",
              "subject": {
                "name": "Coaching EPFL",
                "pageId": "274073285953982",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/10311773_927077923986845_3403559294023312734_n.jpg?oh=77202705c55c9b74dfbc3f0f0efb1a6a&oe=563082A2",
                "type": "Page"
              }
            },
            {
              "id": 2,
              "text": "Zwischenbühne Horw",
              "subject": {
                "name": "Zwischenbühne Horw",
                "pageId": "330485973712339",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-frc3/v/t1.0-9/1234869_493707037390231_1390182891_n.jpg?oh=50305a1fca17212f9f2ce3ed823a4bb0&oe=562F7DE6",
                "type": "Page"
              }
            },
            {
              "id": 0,
              "text": "spood.me",
              "subject": {
                "name": "spood.me",
                "pageId": "229483891391",
                "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/t31.0-8/s720x720/1978503_10152795480756392_1133562003378314118_o.png",
                "type": "Page"
              }
            }
          ],
          "answer": [
            0,
            1,
            2
          ],
          "type": "ORDPageLikeTime",
          "kind": "Order"
        },
        "question3": {
          "subject": {
            "text": "Predestination is one of the few examples where the time travel paradox is well implemented...\\\n",
            "type": "TextPost"
          },
          "min": "2014-11-23T11:53:43+0000",
          "max": "2015-03-23T11:53:43+0000",
          "default": "2014-11-23T11:53:43+0000",
          "unit": "Month",
          "step": 1,
          "threshold": 0,
          "answer": "2015-01-23T11:53:43+0000",
          "type": "TLWhenDidYouShareThisPost",
          "kind": "Timeline"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      },
      {
        "_id": "7fe68a7b1edf66b0dae1aaac",
        "type": "Geolocation",
        "question1": {
          "subject": {
            "text": "All this posts are just to get my master project working\\\n",
            "type": "TextPost"
          },
          "range": 0.02612831795,
          "defaultLocation": {
            "latitude": 46.5502304125624,
            "longitude": 6.579116707970197
          },
          "answer": {
            "latitude": 46.519681242464,
            "longitude": 6.5717116820427
          },
          "type": "GeoWhatCoordinatesWereYouAt",
          "kind": "Geolocation"
        },
        "question2": {
          "subject": {
            "text": "Just bought http://www.manning.com/bernhardt/ can't wait until I will be able to test out play again with the web frontend beeing reactive\\\n",
            "thumbnailUrl": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQCyz2hQVNWsVlI-&w=720&h=720&url=http%3A%2F%2Fmanning.com%2Fimages%2Flogo.gif&cfs=1",
            "url": "http://www.manning.com/bernhardt/",
            "type": "LinkPost"
          },
          "range": 0.02612831795,
          "defaultLocation": {
            "latitude": 46.493302666094806,
            "longitude": 6.555654487666608
          },
          "answer": {
            "latitude": 46.519681242464,
            "longitude": 6.5717116820427
          },
          "type": "GeoWhatCoordinatesWereYouAt",
          "kind": "Geolocation"
        },
        "question3": {
          "subject": {
            "text": "Soon I will be close to finishing all my major master classes and obtain nearly 90% of all my credits I need\\\n",
            "type": "TextPost"
          },
          "range": 0.02612831795,
          "defaultLocation": {
            "latitude": 46.51335850742183,
            "longitude": 6.601552411198722
          },
          "answer": {
            "latitude": 46.519681242464,
            "longitude": 6.5717116820427
          },
          "type": "GeoWhatCoordinatesWereYouAt",
          "kind": "Geolocation"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      },
      {
        "_id": "099a1cec7b072135919f04fb",
        "type": "MultipleChoice",
        "question1": {
          "subject": {
            "text": "Predestination is one of the few examples where the time travel paradox is well implemented...\\\n",
            "type": "TextPost"
          },
          "choices": [
            {
              "text": "Michalina Pacholska",
              "imageUrl": null,
              "fbId": "714035445332109",
              "pageId": null
            },
            {
              "text": "Zelal Al-Shemmery",
              "imageUrl": null,
              "fbId": "768569646537959",
              "pageId": null
            },
            {
              "text": "Aggelos Spyratos",
              "imageUrl": null,
              "fbId": "10204711590929315",
              "pageId": null
            },
            {
              "text": "Pascal Isenegger",
              "imageUrl": null,
              "fbId": "10153155485494466",
              "pageId": null
            }
          ],
          "answer": 2,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "question2": {
          "subject": {
            "text": "I will post some weird things in the future, but its for science so be prepared to see some abnormal activity...\\\n",
            "type": "TextPost"
          },
          "choices": [
            {
              "text": "Zelal Al-Shemmery",
              "imageUrl": null,
              "fbId": "768569646537959",
              "pageId": null
            },
            {
              "text": "Francois Riv",
              "imageUrl": null,
              "fbId": "10152478815799191",
              "pageId": null
            },
            {
              "text": "Bianca Egli Uche",
              "imageUrl": null,
              "fbId": "10154569540755144",
              "pageId": null
            },
            {
              "text": "Romy Hasler",
              "imageUrl": null,
              "fbId": "10204440016983177",
              "pageId": null
            }
          ],
          "answer": 3,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "question3": {
          "subject": {
            "text": "\\\nJohn Doe shared a link.",
            "thumbnailUrl": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQD7rH6nk2VQdben&w=720&h=720&url=https%3A%2F%2Ffbcdn-sphotos-d-a.akamaihd.net%2Fhphotos-ak-xap1%2Fv%2Ft1.0-9%2Fs720x720%2F10991257_10155264188050078_8267303349379896564_n.png%3Foh%3Deabcf538999a30b02a9cf23502293cf0%26oe%3D559548F5%26__gda__%3D1431635790_94e1263c935aad9bb1662dcfd812475b&cfs=1",
            "url": "http://theoatmeal.com/comics/cats_actually_kill",
            "type": "LinkPost"
          },
          "choices": [
            {
              "text": "Andrea Blättler",
              "imageUrl": null,
              "fbId": "10152411515051192",
              "pageId": null
            },
            {
              "text": "Michalina Pacholska",
              "imageUrl": null,
              "fbId": "714035445332109",
              "pageId": null
            },
            {
              "text": "Ramona Pauchard-Batschulat",
              "imageUrl": null,
              "fbId": "10204575155519831",
              "pageId": null
            },
            {
              "text": "Dennis van der Bij",
              "imageUrl": null,
              "fbId": "10204666335476227",
              "pageId": null
            }
          ],
          "answer": 3,
          "type": "MCWhoLikedYourPost",
          "kind": "MultipleChoice"
        },
        "score": 0,
        "answered": false,
        "disabled": false
      }
    ],
    "_id": "fAYrgj74h9dv9Dhmq"
  },
  "status": "ended",
  "playerTurn": 1,
  "player1Scores": 10,
  "player2Scores": 2,
  "boardState": [
    [
      {
        "player": 1,
        "score": 0
      },
      {
        "player": 2,
        "score": 0
      },
      {
        "player": 2,
        "score": 3
      }
    ],
    [
      {
        "player": 2,
        "score": 0
      },
      {
        "player": 2,
        "score": 3
      },
      {
        "player": 1,
        "score": 0
      }
    ],
    [
      {
        "player": 2,
        "score": 1
      },
      {
        "player": 1,
        "score": 1
      },
      {
        "player": 0,
        "score": 0
      }
    ]
  ],
  "player1AvailableMoves": [
    {
      "row": 0,
      "column": 1
    },
    {
      "row": 1,
      "column": 0
    },
    {
      "row": 2,
      "column": 0
    },
    {
      "row": 2,
      "column": 2
    }
  ],
  "player2AvailableMoves": [
    {
      "row": 0,
      "column": 0
    },
    {
      "row": 1,
      "column": 2
    },
    {
      "row": 2,
      "column": 1
    },
    {
      "row": 2,
      "column": 2
    }
  ],
  "wonBy": 1,
  "creationTime": 1445177838
}

"""
}