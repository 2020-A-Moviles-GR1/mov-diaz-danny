/**
 * Chatgroup.js
 *
 * @description :: A model definition represents a database table/collection.
 * @docs        :: https://sailsjs.com/docs/concepts/models-and-orm/models
 */

module.exports = {

  attributes: {

	chat_id: {
      type: 'number',
      required: true
    },
	name_group: {
      type: 'string',
      required: true
    },
	date_creation: {
      type: 'string',
      required: true
    },
	active: {
      type: 'boolean',
	  defaultsTo: true
    },
	mean_delay: {
      type: 'number',
      required: true
    },
    password: {
      type: 'string',
      regex: /^[a-zA-Z]\w{3,14}$/
    },
	messages: {  // One to Many
      collection: 'message',
      via: 'chatGroup'
    }
  },

};

