import 'package:flock_eco_holidays/holiday/holiday_provider.dart';
import 'package:flock_eco_holidays/user/user_provider.dart';
import 'package:flock_eco_holidays/widgets/create_holiday.dart';
import 'package:flock_eco_holidays/widgets/holiday_list.dart';
import 'package:flock_eco_holidays/widgets/sign_in.dart';
import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:provider/provider.dart';

import '../app.dart';

enum Page { SignIn, Holidays }


class MyScaffold extends StatefulWidget {
  @override
  State createState() => MyScaffoldState();
}

class MyScaffoldState extends State<MyScaffold> {
  Page page = Page.Holidays;

  @override
  void initState() {
    super.initState();

    app.googleSingIn.onCurrentUserChanged.listen((GoogleSignInAccount account) {
      var userProvider = Provider.of<UserProvider>(context);
      var holidayProvider = Provider.of<HolidayProvider>(context);

      userProvider.currentUser = account;
      if (account != null) {
        holidayProvider.get();
      }
    });

    app.googleSingIn.signInSilently();
  }

  @override
  Widget build(BuildContext context) {
    var userProvider = Provider.of<UserProvider>(context);
    return Scaffold(
        appBar: AppBar(
          title: Text('Flock Holidays'),
        ),
        body: () {
          switch (page) {
            case Page.Holidays:
              if (userProvider.currentUser == null) {
                return SignIn();
              }
              return HolidayList();
            case Page.SignIn:
              return new SignIn();
          }
        }(),
        floatingActionButton: FloatingActionButton(
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => CreateHoliday(),
                ),
              );
            },
            child: Icon(Icons.add)),
        drawer: Drawer(
          child: ListView(
            // Important: Remove any padding from the ListView.
            padding: EdgeInsets.zero,
            children: <Widget>[
              DrawerHeader(
                child: Text('Flock Holidays', style: TextStyle(fontSize: 23)),
                decoration: BoxDecoration(
                  color: Colors.yellow,
                ),
              ),
              ListTile(
                title: Text('Sign in'),
                leading: Icon(Icons.person_outline),
                onTap: () {
                  setState(() {
                    page = Page.SignIn;
                    Navigator.pop(context);
                  });
                },
              ),
              ListTile(
                title: Text('Holidays'),
                leading: Icon(Icons.wb_sunny),
                onTap: () {
                  setState(() {
                    page = Page.Holidays;
                    Navigator.pop(context);
                  });
                },
              ),
            ],
          ),
        ));
  }
}