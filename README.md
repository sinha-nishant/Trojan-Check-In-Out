# Trojan-Check-In-Out

The system is an Android app, called Trojan Check In/Out, to limit the number of people entering and exiting indoor places on the USC Campus, in order to enforce strict, proper and precise safety regulations in response to the COVID-19 pandemic. In the past, these indoor places have tried to manually keep track of the people entering and leaving, but the inefficiency of few enforcers trying to regulate many people entering and leaving these places, has resulted in the necessity of this app – one capable of counting and tracking people more efficiently.

The app features a manager account and a student account, both of which are USC-email verified accounts. The manager account is able to add or remove buildings and enforce the capacity limit either through the UI or by importing a CSV file. The manager account can also see the list of people in each building and open each student’s profile, search the list of people through various criteria, check the profile of each student and generate QR-code for each building to be scanned by the students. The student account requires the name, ID and major in order to sign up, apart from the USC-email. The student account displays the profile consisting of the name, ID, picture and major. The students can update their password and their profile pictures. Through the app, the student scans the QR-code in order to check available capacity, check out by rescanning the QR-code, and the student cannot check-in into multiple buildings without checking-out from the previous building. All of these features are implemented through easy to understand UI features.

The system is useful for fast moving indoor places, as well as places with large numbers of people, such as Trader Joe’s and Target. For fast moving indoor places, humans would inherently be imprecise in keeping track of the people moving in and out at a fast pace, and could create major errors in maintaining the count, thereby jeopardizing the health of the people. For a large number of people, keeping track of the people itself would require multiple enforcers, apart from the process being inconvenient and slow. Both of these problems are solved by the app which requires only one manager, is faster, more secure and, overall, more efficient.

A similar system implemented on the USC campus is by the USC housing. This system uses an IAM (Identity Access Management) server to keep track of all the users living in different buildings. It allows authentication using face and fingerprint. This system is slow and tedious as the amount of time to check in a single user can vary between 3 seconds to 1 minute, and during fire drills a single person might have to wait 30 minutes to get back into the building (after the drill is over). Wearing a mask or gloves would result in longer waiting times. Scaling is even worse, bigger classes and libraries at USC require people to get in and out of buildings instantly, so that they can focus on studying rather than waiting in lines. Trojan Check In/Out is similar, but scalable and faster, as the authentication requires only reading a QR code using the system, so there is no inefficiency of the physical fingerprint scanner or facial scanner. The system also minimizes the amount of workers and physical IAM systems required to be present, thereby, decreasing both the fixed and variable costs.


# How to Run our application:
**Step 1: Setting up app in Android Studio**
<br/>
- Unzip the zip file
- You should see a Trojan-Check-In-Out folder. This contains our app
- Go to android studio. On the landing page, click on "Open an Existing Project"
- Find the Trojan-Check-In-Out folder on your device, click into it, then click into the android folder and then press the open button to set this as your project.
- Once in Android Studio, on the topbar, click on tools-> SDK Manager . You should now be able to see this:
![image](https://user-images.githubusercontent.com/42727780/111896006-9789e680-8a3c-11eb-9d24-e415ef46b3a6.png)
- Please make sure to select Android 10.0 only
- For all emulators used, make sure you use one compatible with API 29

**Step 1: Initializing AWS Amplify Plugin**
<br/>
- You will need to download some plugins and get access to some authorization keys to get access to AWS S3 which we use to store our images. These are the steps to do so:
- Go to your the command line on your device
- For mac/Linux enter: curl -sL https://aws-amplify.github.io/amplify-cli/install | bash && $SHELL
- For Windows enter: curl -sL https://aws-amplify.github.io/amplify-cli/install-win -o install.cmd && install.cmd
- Once these commands finish executing, enter the term "amplify configure" into the commandline
- The following image shows you the entire commandline process:
![image](https://user-images.githubusercontent.com/42727780/111902753-eb102a80-8a64-11eb-9a86-7359d6e09dc6.png)

-When asked to Sign in to your AWS administrator account, press enter. You will be redirected to the following page
![image](https://user-images.githubusercontent.com/42727780/111902805-2dd20280-8a65-11eb-9dc0-1f965c4e9e2c.png)
- Please sign in using the email: mitraar@usc.edu and password:Password123
- Go back to the terminal and answer the questions as follows: region : us-west-2, profile: Enter your name
- After this you should be redirected back by AWS console to the following page
![image](https://user-images.githubusercontent.com/42727780/111902947-ce282700-8a65-11eb-80df-1eda17181634.png)
-Please click the next button again and again till you see this
![image](https://user-images.githubusercontent.com/42727780/111902971-eef07c80-8a65-11eb-9d5d-07680faec1cb.png)
- Now click on Create user which will result you in seeing this page
![image](https://user-images.githubusercontent.com/42727780/111903006-12b3c280-8a66-11eb-9271-f2f44fdbb0b9.png)
- Place take a note of the ID and key
- go back to the terminal. Enter in your id and access key when prompted to do so
- Once done, you will be prompted to name your profile. Just name it something easy to remember
- Once done, you should get a success message on terminal. You can now close the terminal. 
- Pull up our project on android studio
- In android studio pull up the terminal tab at the bottom of your page. Write the term "amplify init" into the Android Studio terminal. Please answer the following questions in the same vain as below
![image](https://user-images.githubusercontent.com/42727780/111903137-c0bf6c80-8a66-11eb-8c49-a8e34474142d.png)
- Replace angadProfile with the profile name you set earlier when your are asked for your profile
- If you do everything right, you should get the same success message in terminal as the provided picture

# You now have everything set up in order to run our app




