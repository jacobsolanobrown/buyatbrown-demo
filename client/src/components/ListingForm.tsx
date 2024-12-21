import React, { useEffect, useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { PulseLoader } from "react-spinners";

interface PostingPageProps {
  uid: string;
  username: string;
}

const PostingPage: React.FC<PostingPageProps> = ({ uid, username }) => {
  const navigate = useNavigate();
  const [responseMessage, setResponseMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    uid: uid || "", // Initialize with passed uid
    title: "",
    price: "",
    username: username || "", // Initialize with passed username
    description: "",
    condition: "",
    category: "",
    tags: "",
    imageFile: null,
  });
  // setFormData({ ...formData, uid: uid, username: username });

  // Handle form input changes
  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  // Handle form image changes
  const handleImageChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const file = e.target.files[0];
    setFormData({ ...formData, imageFile: file });
  };

  /**
 * This handles form submission, validating all of the inputs and whether we can post a listing. It handles posting an image to our 
 * separate logic for i,mage posting.
 * 
 * @param e The event when clicking the submit button on the form 
 * @returns On success, it should return the user back to the home screen 
 * // TODO: add a success screen for posting and allow the user to navigate to their listings 

 */
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Handle form submission logic here
    console.log("Form data submitted:", formData);
    if (!formData.imageFile) {
      setResponseMessage("Please upload an image.");
      return;
    }

    setIsSubmitting(true);
    setResponseMessage("");

    try {
      // Convert image to Base64
      const reader = new FileReader();
      reader.readAsDataURL(formData.imageFile);
      reader.onload = async () => {
        // Error check the reader result to prevent null pointer exception
        const base64Image = reader.result ? reader.result.split(",")[1] : ""; // Strip out metadata

        // Prepare form data
        const data = {
          ...formData,
          imageUrl: base64Image,
        };

        try {
          const response = await axios.post(
            "http://localhost:3232/add-listings",
            data.imageUrl, // Send the image URL as raw body
            {
              headers: { "Content-Type": "text/plain" },
              params: {
                uid: data.uid,
                username: data.username,
                price: data.price,
                title: data.title,
                category: data.category,
                tags: data.tags,
                condition: data.condition,
                description: data.description,
              },
            }
          );
          if (response.data.response_type === "success") {
            setResponseMessage("Listing added successfully!");
            // Reset form data
            setFormData({
              uid: uid || "",
              title: "",
              price: "",
              username: username || "",
              description: "",
              condition: "",
              category: "",
              tags: "",
              imageFile: null,
            });
          } else {
            setResponseMessage(`Error: ${response.data.error}`);
          }
        } catch (error) {
          console.error("Error uploading listing:", error);
          setResponseMessage("An error occurred while uploading the listing.");
        }
      };
    } catch (error) {
      console.error("Error uploading listing:", error);
      setResponseMessage("An error occurred while uploading the listing.");
    } finally {
      setIsSubmitting(false);
      console.log("Form data submitted twice:", formData);
      navigate("/");
    }
  };

  const goBack = () => {
    navigate("/");
  };

  return (
    <div className="flex flex-col align-center items-center min-h-screen bg-gradient-to-r from-blue-100 to-pink-100">
      <div className="w-3/4 mx-12 my-14 p-8 rounded-3xl shadow-lg  bg-white/50 ">
        <button className="py-4 px-2 underline" onClick={goBack}>
          Cancel
        </button>
        <h1 className="text-3xl font-bold mb-6 ml-2">Post a New Listing</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label
              htmlFor="title"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Listing Title
            </label>
            <input
              type="text"
              id="Listing Title"
              name="title"
              placeholder="Choose a listing title"
              value={formData.title}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6  py-4"
              required
            />
          </div>
          {/* Input for price */}
          <div>
            <label
              htmlFor="price"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Price
            </label>
            <input
              type="text"
              id="price"
              name="price"
              placeholder="Choose a price"
              value={formData.price}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6  py-4"
              required
            />
          </div>
          {/* Input for imageUrl */}
          <div>
            <label
              htmlFor="imageUrl"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Photo
            </label>
            <input
              type="file"
              accept="image/png, image/jpeg"
              id="imageUrl"
              name="imageUrl"
              placeholder="Upload an image (png or jpeg)"
              onChange={handleImageChange}
              className="mt-2 block w-full border-gray-300  px-2 py-2 rounded-full"
              required
            />
          </div>
          {/* Input for a description */}
          <div>
            <label
              htmlFor="description"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Description
            </label>
            <div>
              <textarea
                id="description"
                name="description"
                placeholder="Describe your listing"
                value={formData.description}
                onChange={handleChange}
                className=" block w-full border border-gray-300 rounded-xl shadow-sm px-6  py-4"
                required
              />
            </div>
          </div>
          {/* Dropdown menu to choose item condition */}
          <div>
            <label
              htmlFor="condition"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Condition
            </label>
            <select
              id="condition"
              name="condition"
              value={formData.condition}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm pl-6 pr-6 py-4 bg-white"
              required
            >
              <option value="" className="text-gray-400" disabled>
                Select a condition
              </option>
              <option value="New">New</option>
              <option value="Like New">Like New</option>
              <option value="Used">Used</option>
            </select>
          </div>
          {/* Dropdown menu to choose item category */}
          <div>
            <label
              htmlFor="category"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Category
            </label>
            <select
              id="category"
              name="category"
              value={formData.category}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm pl-6 pr-6 py-4 bg-white"
              required
            >
              <option value="" className="text-gray-400 " disabled>
                Select a category
              </option>
              <option value="Clothes">Clothes</option>
              <option value="Tech">Tech</option>
              <option value="School">School</option>
              <option value="Furniture">Furniture</option>
              <option value="Kitchen">Kitchen</option>
              <option value="Bathroom">Bathroom</option>
              <option value="Misc">Miscellaneous</option>
            </select>
          </div>
          {/* Add tags */}
          <div>
            <label
              htmlFor="tags"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Tags
            </label>
            <input
              type="text"
              id="tags"
              name="tags"
              placeholder="Add tags"
              value={formData.tags}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6  py-4"
              required
            />
          </div>
          {/* Submit the listing (Call the Create Listing API endpoint) */}
          {isSubmitting ? (
            <button className="w-full bg-blue-600 text-white py-4 px-6  rounded-full hover:bg-blue-700 text-xl font-bold">
              <PulseLoader
                color="#FFFFF"
                margin={4}
                size={10}
                speedMultiplier={0.7}
              />
            </button>
          ) : (
            <button
              type="submit"
              className="w-full bg-blue-600 text-white py-4 px-6  rounded-full hover:bg-blue-700 text-xl font-bold"
            >
              Post Listing
            </button>
          )}
        </form>
      </div>
    </div>
  );
};

export default PostingPage;
