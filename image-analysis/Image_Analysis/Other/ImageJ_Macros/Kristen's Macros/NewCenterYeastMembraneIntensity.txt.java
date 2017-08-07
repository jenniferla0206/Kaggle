//FUNICTIONS

//Returns the maximum of the array
function maxOfArray(array) {
    min=0;
    for (a=0; a<lengthOf(array); a++) {
        min=minOf(array[a], min);
    }
    max=min;
    for (a=0; a<lengthOf(array); a++) {
        max=maxOf(array[a], max);
    }
    return max;
}

//Returns the minimum of the array
function minOfArray(array) {
    max=0;
    for (a=0; a<lengthOf(array); a++) {
        max=maxOf(array[a], max);
    }
    min=max;
    for (a=0; a<lengthOf(array); a++) {
        min=minOf(array[a], min);
    }
    return min;
}

//Returns the indices at which a value occurs within an array 
function indexOfArray(array) { 
    count=0; 	
    for (i=0; i<lengthOf(array); i++) { 
        if (array[i]==value) { 
            count++; 
        } 
    } 
    if (count>0) { 
        //indices=newArray(count); 
        count=0; 
        for (i=0; i<lengthOf(array); i++) { 
            if (array[i]==value) { 
                indices=i; 
                count++; 
            } 
        } 
		return indices;  
	}
}

//Prints the array in a human-readable form
function printArray(array) {
    string="";
    for (i=0; i<lengthOf(array); i++) {
        if (i==0) {
            string=string+array[i];
        } else {
            string=string+", "+array[i];
        }
    }
    print(string);
}

selectWindow("Duplicate");
run("Median...", "radius=1 stack");

msg="Choose the x,y coordinate opposite the bud site by drawing a circle around the outside of the cell.";
waitForUser(msg + "\n" + "When finished, press OK");
roiManager("Add");
List.setMeasurements;
print(List.getList);
outerX=List.getValue("X");
outerY=List.getValue("Y");

msg="Choose the x,y coordinate opposite the bud site by drawing a circle at that point.";
waitForUser(msg + "\n" + "When finished, press OK");
roiManager("Add");
List.setMeasurements;
print(List.getList);
memX=List.getValue("X");
memY=List.getValue("Y");

makeLine(outerX, outerY, memX, memY, 1);


waitForUser("Choose a directory to save your straightened snake lines");
tmp=getDirectory("Choose a Directory");
snakeDir=tmp+"Straight_Snakes"+File.separator;
File.makeDirectory(snakeDir);
  if (!File.exists(snakeDir))
      exit("Unable to create directory");
  print("");
  print(snakeDir);
  
//waitForUser("Choose the directory containing your Snakes file.");
dir=tmp+"Current Snake";
//IJ.log(dir);
traces=getFileList(dir);
selectWindow("Duplicate");
f=nSlices();
//IJ.log("#frames=" + f);
	//f for frame
r=nResults();
cellNumber=newArray(f+1);
numRows=0;

maxInt=newArray(f+1);
maxNormInt=newArray(f+1);
maxCoord=newArray(f+1);
minInt=newArray(f+1);
minCoord=newArray(f+1);

setBatchMode(true);
				
for (n=1; n<=f; n++) {
	setSlice(n);
	cellNumber[n]=n;
	//IJ.log("n=" + n);
			
		for (i=0; i<traces.length; i++) {
			//IJ.log("traces=" + traces.length);
			string=File.openAsString(dir + traces[i]);
			rows=split(string, "\n");
			//IJ.log("#rows=" + rows.length);
			counter=0;
		
			for(j=0; j<rows.length; j++){
				column=split(rows[j], "\t");
			
				if (column[0]==n) {
					counter++;		
				}
			}
			//IJ.log("numRows=" + numRows);
			//IJ.log("counter=" + counter);
			x=newArray(rows.length);
			y=newArray(rows.length);
			distTmp=newArray(rows.length);
		
			for(j=numRows; j<rows.length; j++){
				column=split(rows[j], "\t");
				xcoordTmp=newArray(counter);
				ycoordTmp=newArray(counter);
				//dist=newArray(counter);
				
				if (column[0]==n) { 
					x[j]=parseFloat(column[2]);
					y[j]=parseFloat(column[3]);
					xPt=parseFloat(column[2]);
 					yPt=parseFloat(column[3]);
 					distTmp[j]=sqrt((abs(xPt-memX))^2+(abs(yPt-memY))^2);
					numRows++;
					//IJ.log("numRows Again="+numRows);
 				}
 				
 			for(j=numRows; j<rows.length; j++){
				column=split(rows[j], "\t");
				//xcoordTmp=newArray(counter);
				//ycoordTmp=newArray(counter);
				dist=newArray(counter);
				
				if (column[0]==n) { 
					//x[j]=parseFloat(column[2]);
					//y[j]=parseFloat(column[3]);
					//xPt=parseFloat(column[2]);
 					//yPt=parseFloat(column[3]);
 					distTmp[j]=sqrt((abs(xPt-memX))^2+(abs(yPt-memY))^2);
					numRows++;
					//IJ.log("numRows Again="+numRows);
 				}
 				
 				if (n>1) {
 					start=numRows-counter;
 				} else {
 					start=0;
 				}
 				//IJ.log("start=" + start);
 				xcoordTmp=Array.slice(x, start, numRows);
 				ycoordTmp=Array.slice(y, start, numRows);
 				dist=Array.slice(distTmp, start, numRows);
 				//IJ.log("distTmp="+distTmp[j]);
 				}
 				
 			//IJ.log("dist=");
 			//Array.print(dist);
 			//printArray(dist);
 			value=minOfArray(dist);
 			indexMin=indexOfArray(dist);
 			end=indexMin-1;
 			//IJ.log("min of dist="+value);
 			//IJ.log("index of min="+indexMin);
 			xSplit1=Array.slice(xcoordTmp, indexMin, counter);
 			xSplit2=Array.slice(xcoordTmp, 0, end);
 			xcoord=Array.concat(xSplit1, xSplit2);
 			//printArray(xcoord);
 			ySplit1=Array.slice(ycoordTmp, indexMin, counter);
 			ySplit2=Array.slice(ycoordTmp, 0, end);
 			ycoord=Array.concat(ySplit1, ySplit2);
 			//printArray(ycoord);
 			
 			makeSelection("polyline", xcoord, ycoord);
 			roiManager("Add");	
			title=getSliceNumber();
			//IJ.log("title="+title);
			run("Interpolate", "interval=1");
			run("Straighten...", "line=6");
			selectWindow("Duplicate-1");
			w=getWidth;
			//IJ.log(x);
			if (w<100) {
				run("Size...", "width=100 height=6 average interpolation=Bilinear");
			}
			saveAs("Tiff", snakeDir+title);
			//makeLine(25,3,75,3,6);
			makeLine(0,3,100,3,6);
			profileBudInt=getProfile();
			//IJ.log("profileBudInt=");
			//printArray(profileBudInt);
			for (k=0; k<profileBudInt.length; k++) {
				intensity=profileBudInt[k];
				//IJ.log("Intensity="+intensity);
				setResult("Profile XCoord", k, k);
				setResult("Plot Profile_"+n, k, intensity);
			}
			
			selectWindow("Duplicate");
		}
	value=maxOfArray(profileBudInt);
	//IJ.log("Max="+value);
	//IJ.log("Max="+max);
	indexMax=indexOfArray(profileBudInt);
	max=value;
	value=minOfArray(profileBudInt);
	//IJ.log("Min="+value);
	indexMin=indexOfArray(profileBudInt);
	//background=maxOfArray(profileBackInt);
	normValue=max/value;
	//IJ.log("normValue="+normValue);
	setResult("TimePoint", n-1, n);
	setResult("Profile Max X-Coordinate", n-1, indexMax);
	setResult("Profile Max Int", n-1, max);
	setResult("Profile Min X-Coordinate", n-1, indexMin);
	setResult("Profile Min Int", n-1, value);
	setResult("Profile Max Norm Int", n-1, normValue);
	maxInt[n]=max;
	minInt[n]=value;
	maxNormInt[n]=normValue;
	maxCoord[n]=indexMax;
	minCoord[n]=indexMin;
	
}

setBatchMode(false);

IJ.log("maxInt=");
printArray(maxInt);
IJ.log("maxCoord=");
printArray(maxCoord);
maxInt=maxOfArray(maxInt);
value=maxOfArray(maxNormInt);
timeAtMax=indexOfArray(maxNormInt);
IJ.log("Profile Maximum Intensity="+maxInt);
IJ.log("Profile Maximum Normalized Intensity="+value);
IJ.log("Profile TimePoint of Norm Max Intensity="+timeAtMax);

//stack=getDirectory("Choose a directory");
stack=tmp+"Straight_Snakes";
run("Image Sequence...", "open=stack number=121 starting=1 increment=1 scale=100 file=[] or=[] sort");
run("Make Montage...", "columns=1 rows=121 scale=1 first=1 last=121 increment=1 border=0 font=12");
roiManager("Save",tmp+"ROIs.zip");
