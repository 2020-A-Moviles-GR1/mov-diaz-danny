/**
 * FileController
 *
 * @description :: Server-side logic for managing files
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

module.exports = {



  /**
   * `FileController.upload()`
   *
   * Upload file(s) to the server's disk.
   */
  upload: function (req, res) {

    // e.g.
    // 0 => infinite
    // 240000 => 4 minutes (240,000 miliseconds)
    // etc.
    //
    // Node defaults to 2 minutes.
    res.setTimeout(0);

    req.file('file')
    .upload({

      // You can apply a file upload limit (in bytes)
      maxBytes: 10000000

    }, function whenDone(err, uploadedFiles) {
      if (err) return res.serverError(err);
	  
      else{
		  console.log(uploadedFiles)
		  
		  var image = uploadedFiles[0];
		  if(image == undefined){
			  return res.json({
				  id_message: -1,
				  server_message: "incorrecto",
				  metadata: "",
				  error: "",
				  data: [],
			  });
		  }
		  console.log(image.fd)
		  var full_dir = ""+image.fd;
		  var split = full_dir.split("\\")
		  var name = split[split.length-1];
		  
		  return res.json({
			  id_message: 1,
			  server_message: "correcto",
			  metadata: "http://localhost:1337/public/images?path="+name,
			  error: "",
			  data: [image.fd],
		  });
	  }
    });
  },

  /**
   * `FileController.s3upload()`
   *
   * Upload file(s) to an S3 bucket.
   *
   * NOTE:
   * If this is a really big file, you'll want to change
   * the TCP connection timeout.  This is demonstrated as the
   * first line of the action below.
   */
  s3upload: function (req, res) {

    // e.g.
    // 0 => infinite
    // 240000 => 4 minutes (240,000 miliseconds)
    // etc.
    //
    // Node defaults to 2 minutes.
    res.setTimeout(0);

    req.file('file').upload({
      adapter: require('skipper-s3'),
      bucket: process.env.BUCKET,
      key: process.env.KEY,
      secret: process.env.SECRET
    }, function whenDone(err, uploadedFiles) {
      if (err) return res.serverError(err);
      else return res.json({
        files: uploadedFiles,
        textParams: req.allParams()
      });
    });
  },


  /**
   * FileController.download()
   *
   * Download a file from the server's disk.
   */
  download: function (req, res) {
    var Path = require('path');
    var fs = require('fs');

    // If a relative path was provided, resolve it relative
    // to the cwd (which is the top-level path of this sails app)
    fs.createReadStream(Path.resolve(req.param('path')))
    .on('error', function (err) {
      return res.serverError(err);
    })
    .pipe(res);
  }
};

