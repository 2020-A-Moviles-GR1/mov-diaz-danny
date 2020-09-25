/**
 * Message.js
 *
 * @description :: A model definition represents a database table/collection.
 * @docs        :: https://sailsjs.com/docs/concepts/models-and-orm/models
 */

module.exports = {

  attributes: {
	  
	sender: {
      type: 'string',
      required: true
    },
	message_id: {
      type: 'number',
      required: true
    },
	content: {
      type: 'string',
      required: true
    },
	date_creation: {
      type: 'string',
      required: true
    },
	modified: {
      type: 'boolean',
	  defaultsTo: false
    },
	ping_registered: {
      type: 'number',
      required: true
    },
	chatGroup: {
      model: 'Chatgroup',
      required: false
    },
	latitud: {
      type: 'number',
      required: true
    },
	longitud: {
      type: 'number',
      required: true
    },
	url_web: {
      type: 'string',
      required: true
    },
	url_image: {
      type: 'string',
      required: true
    },
  },

};

